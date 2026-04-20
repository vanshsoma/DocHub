let currentUser = null;
let currentDocument = null;
let stompClient = null;
let documentVersion = 1;

// DOM Elements
const loginView = document.getElementById('login-view');
const dashboardView = document.getElementById('dashboard-view');
const editorView = document.getElementById('editor-view');
const loginBtn = document.getElementById('login-btn');
const logoutBtn = document.getElementById('logout-btn');
const newDocBtn = document.getElementById('new-doc-btn');
const backBtn = document.getElementById('back-btn');
const docList = document.getElementById('document-list');
const syncStatus = document.getElementById('sync-status');
const versionDisplay = document.getElementById('version-display');
const typingIndicator = document.getElementById('typing-indicator');
const toastContainer = document.getElementById('toast-container');

// Utilities
const showView = (viewElem) => {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    viewElem.classList.add('active');
};

function showToast(message) {
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerHTML = `<span>${message}</span> <button>&times;</button>`;
    toast.querySelector('button').onclick = () => toast.remove();
    toastContainer.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
}

// Authentication
loginBtn.addEventListener('click', async () => {
    const un = document.getElementById('username').value;
    const pw = document.getElementById('password').value;
    const err = document.getElementById('login-error');
    err.innerText = '';

    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: un, password: pw })
        });
        if (res.ok) {
            currentUser = await res.json();
            document.getElementById('welcome-user').innerText = `Hello, ${currentUser.username} (${currentUser.role})`;
            loadDashboard();
        } else {
            err.innerText = 'Invalid credentials';
        }
    } catch (e) {
        err.innerText = 'Login Failed';
    }
});

logoutBtn.addEventListener('click', () => {
    currentUser = null;
    showView(loginView);
    disconnectWebSocket();
});

// Dashboard
async function loadDashboard() {
    showView(dashboardView);
    // Show admin button only for ADMIN users
    const adminBtn = document.getElementById('admin-panel-btn');
    if (currentUser && currentUser.role === 'ADMIN') {
        adminBtn.style.display = 'inline-block';
    } else {
        adminBtn.style.display = 'none';
    }
    const res = await fetch('/api/documents');
    const docs = await res.json();
    docList.innerHTML = '';
    docs.forEach(doc => {
        const card = document.createElement('div');
        card.className = 'doc-card glass-panel';
        const isOwner = doc.owner && doc.owner.userId === currentUser.userId;
        const isAdmin = currentUser.role === 'ADMIN';
        let deleteBtnHtml = (isOwner || isAdmin) ? `<button class="delete-doc-btn" data-id="${doc.documentId}">Delete</button>` : '';

        card.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: start;">
                <h3>${doc.title}</h3>
                ${deleteBtnHtml}
            </div>
            <p>Author: ${doc.owner ? doc.owner.username : 'Unknown'}</p>
            <span class="doc-status">${doc.status}</span>
        `;
        card.addEventListener('click', (e) => {
            if (e.target.classList.contains('delete-doc-btn')) {
                e.stopPropagation();
                deleteDocument(doc.documentId);
            } else {
                openDocument(doc.documentId);
            }
        });
        docList.appendChild(card);
    });
}

async function deleteDocument(docId) {
    if (!confirm("Are you sure you want to delete this document?")) return;
    const res = await fetch(`/api/documents/${docId}?userId=${currentUser.userId}`, { method: 'DELETE' });
    if (res.ok) {
        showToast("Document deleted successfully.");
        loadDashboard();
    } else {
        showToast("Error executing deletion or Unauthorized.");
    }
}

newDocBtn.addEventListener('click', async () => {
    const title = prompt("Enter Document Title:");
    if (!title) return;
    const res = await fetch('/api/documents', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, content: '', userId: currentUser.userId })
    });
    if (res.ok) {
        const doc = await res.json();
        openDocument(doc.documentId);
    }
});

// Editor & WebSocket
async function openDocument(docId) {
    const res = await fetch(`/api/documents/${docId}?userId=${currentUser.userId}`);
    if (res.ok) {
        currentDocument = await res.json();
        documentVersion = currentDocument.version;
        document.getElementById('editor-doc-title').innerText = currentDocument.title;
        document.getElementById('doc-status-badge').innerText = currentDocument.status;
        
        // Show view BEFORE initializing Quill so it can calculate dimensions correctly
        showView(editorView);
        
        // Ensure Quill is initialized only once
        if (!window.quill) {
            if (typeof QuillCursors !== 'undefined') {
                Quill.register('modules/cursors', QuillCursors);
            }
            window.quill = new Quill('#editor-container', {
                theme: 'snow',
                modules: {
                    cursors: true,
                    toolbar: false
                }
            });
            window.cursors = window.quill.getModule('cursors');
            window.myColor = '#' + Math.floor(Math.random()*16777215).toString(16).padStart(6, '0');
            setupQuillEvents();
        }
        
        // Use Quill's API to paste HTML with an 'api' flag to avoid feedback loops
        window.quill.clipboard.dangerouslyPasteHTML(currentDocument.content || '', 'api');
        
        versionDisplay.innerText = `Version: ${documentVersion}`;
        
        // Disconnect existing socket before opening a new document connection
        disconnectWebSocket();
        
        // Connect WS
        connectWebSocket();
    }
}

function disconnectWebSocket() {
    if (stompClient !== null) {
        try { stompClient.disconnect(); } catch (e) { /* ignore */ }
        stompClient = null;
    }
}

function connectWebSocket() {
    const socket = new SockJS('/ws-editor');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Disable debug output
    
    syncStatus.innerText = '● Connecting...';
    syncStatus.className = 'sync-status';

    stompClient.connect({}, function (frame) {
        syncStatus.innerText = '● Connected';
        syncStatus.className = 'sync-status';
        console.log('WebSocket connected:', frame);
        
        // Subscribe to document updates (version tracking / persistence confirmation)
        stompClient.subscribe(`/topic/document/${currentDocument.documentId}`, function (message) {
            const data = JSON.parse(message.body);
            if (data.status === 'SUCCESS') {
                documentVersion = data.newVersion;
                versionDisplay.innerText = `Version: ${documentVersion}`;
            } else if (data.status === 'CONFLICT') {
                showToast("Conflict detected! Reloading document to get latest edits.");
                openDocument(currentDocument.documentId);
            }
        });
        
        // Subscribe to cursor indicators
        stompClient.subscribe(`/topic/document.cursor/${currentDocument.documentId}`, function (message) {
            const data = JSON.parse(message.body);
            if (data.username !== currentUser.username) {
                try {
                    // cursors() returns an array of cursor objects, each with an .id property
                    const existingCursors = window.cursors.cursors();
                    const found = existingCursors.some(c => c.id === data.username);
                    if (!found) {
                        window.cursors.createCursor(data.username, data.username, data.color);
                    }
                    window.cursors.moveCursor(data.username, { index: data.index, length: 0 });
                } catch (e) {
                    console.warn('Cursor update failed:', e);
                }
            }
        });
        
        // Subscribe to structural OT Deltas for flawless simultaneous typing
        stompClient.subscribe(`/topic/document.delta/${currentDocument.documentId}`, function (message) {
            const data = JSON.parse(message.body);
            if (data.userId !== currentUser.userId) {
                try {
                    const deltaObj = JSON.parse(data.deltaJson);
                    window.quill.updateContents(deltaObj, 'api');
                } catch (e) {
                    console.warn('Delta apply failed:', e);
                }
            }
        });

        // Register session
        stompClient.send(`/app/document.register/${currentDocument.documentId}`, {}, JSON.stringify({
            userId: currentUser.userId
        }));
    }, function(error) {
        console.error('WebSocket connection error:', error);
        syncStatus.innerText = '● Offline';
        syncStatus.className = 'sync-status offline';
        showToast('WebSocket disconnected. Trying to reconnect...');
        
        // Auto-reconnect after 3 seconds
        setTimeout(() => {
            if (currentDocument) {
                connectWebSocket();
            }
        }, 3000);
    });
}

// Setup events after editor loaded
function setupQuillEvents() {
    let editTimeout = null;

    window.quill.on('text-change', (delta, oldDelta, source) => {
        // Ignore changes triggered by our own 'api' calls
        if (!stompClient || !stompClient.connected || !currentDocument || source === 'api') return;
        
        // Send instantaneous OT Deltas for flawless peer typing visual
        try {
            stompClient.send(`/app/document.delta/${currentDocument.documentId}`, {}, JSON.stringify({
                userId: currentUser.userId,
                deltaJson: JSON.stringify(delta)
            }));
        } catch (e) {
            console.warn('Failed to send delta:', e);
        }
        
        clearTimeout(editTimeout);
        editTimeout = setTimeout(() => {
            if (!stompClient || !stompClient.connected || !currentDocument) return;
            // Send full backup to cleanly persist in the database
            try {
                stompClient.send(`/app/document.edit/${currentDocument.documentId}`, {}, JSON.stringify({
                    userId: currentUser.userId,
                    newContent: window.quill.root.innerHTML,
                    baseVersion: documentVersion
                }));
            } catch (e) {
                console.warn('Failed to send edit:', e);
            }
        }, 800);
    });

    // Send cursor movements
    window.quill.on('selection-change', (range) => {
        if (range && stompClient && stompClient.connected && currentDocument) {
            try {
                stompClient.send(`/app/document.cursor/${currentDocument.documentId}`, {}, JSON.stringify({
                    username: currentUser.username,
                    color: window.myColor,
                    index: range.index
                }));
            } catch (e) {
                console.warn('Failed to send cursor:', e);
            }
        }
    });
}

backBtn.addEventListener('click', () => {
    disconnectWebSocket();
    loadDashboard();
});

// ===== ADMIN PANEL =====
const adminView    = document.getElementById('admin-view');
const adminPanelBtn = document.getElementById('admin-panel-btn');
const adminBackBtn  = document.getElementById('admin-back-btn');

adminPanelBtn.addEventListener('click', () => loadAdminPanel());
adminBackBtn.addEventListener('click', () => loadDashboard());

async function loadAdminPanel() {
    showView(adminView);

    // Load metrics
    const metricsRes = await fetch(`/api/admin/metrics?userId=${currentUser.userId}`);
    if (metricsRes.ok) {
        const metrics = await metricsRes.json();
        const metricDefs = [
            { key: 'totalEvents',  label: 'Total Events',   icon: '📊' },
            { key: 'logins',       label: 'Logins',         icon: '🔑' },
            { key: 'edits',        label: 'Edits',          icon: '✏️' },
            { key: 'creates',      label: 'Docs Created',   icon: '📄' },
            { key: 'archives',     label: 'Archived',       icon: '📦' },
            { key: 'opens',        label: 'Doc Opens',      icon: '👁' },
        ];
        const grid = document.getElementById('metrics-grid');
        grid.innerHTML = metricDefs.map(d => `
            <div class="metric-card">
                <div class="metric-icon">${d.icon}</div>
                <div class="metric-value">${metrics[d.key] ?? 0}</div>
                <div class="metric-label">${d.label}</div>
            </div>
        `).join('');
    }

    // Load activity log
    const eventsRes = await fetch(`/api/admin/activity?userId=${currentUser.userId}`);
    if (eventsRes.ok) {
        const events = await eventsRes.json();
        const tbody = document.getElementById('activity-log-body');
        if (events.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color: var(--text-muted); padding: 2rem;">No activity yet.</td></tr>`;
        } else {
            tbody.innerHTML = events.map((e, i) => `
                <tr>
                    <td style="color: var(--text-muted);">${i + 1}</td>
                    <td><span class="event-badge ${e.eventType}">${e.eventType.replace('_', ' ')}</span></td>
                    <td>${e.user ? e.user.username : '—'}</td>
                    <td>${e.relatedDocumentId !== null ? '#' + e.relatedDocumentId : '—'}</td>
                    <td style="color: var(--text-muted); font-size: 0.82rem;">${new Date(e.timestamp).toLocaleString()}</td>
                </tr>
            `).join('');
        }
    }
}
