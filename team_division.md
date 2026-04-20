# DocHub — Team Division Plan (4 Members)

> **Project:** DocHub — Real-time Collaborative Document Editor  
> **Stack:** Spring Boot (MVC) + WebSockets + H2/MySQL + Vanilla JS Frontend  
> **Constraint:** Each member owns 1 Major + 1 Minor use case end-to-end (Model → Repository → Service → Controller → UI)

---

## 🎨 Design Patterns (4 Total)

| # | Pattern Type | Pattern Name | Where Used | Owner |
|---|-------------|--------------|------------|-------|
| 1 | **Framework-enforced** | **MVC Pattern** | Spring Boot's Controller-Service-Repository structure | All |
| 2 | **Creational** | **Factory Method** | `DocumentFactory` — creates Documents with default state (DRAFT, version=1) | Member 1 |
| 3 | **Structural** | **Facade Pattern** | `CollaborationFacade` — wraps CollaborationService + DocumentService + ActivityTracker into a single unified API | Member 3 |
| 4 | **Behavioral** | **Observer Pattern** | `ActivityTracker` as observer — services notify it on every significant action (login, edit, archive) | Member 4 |

---

## 📐 Design Principles (8 Total — 1 SOLID + 1 GRASP per member)

### SOLID Principles

| Member | Principle | How It's Applied |
|--------|-----------|------------------|
| 1 | **SRP** — Single Responsibility | `AuthService` only authenticates/authorizes. `DocumentService` only manages CRUD. No class does two jobs. |
| 2 | **OCP** — Open/Closed | `DocumentStatus` enum (DRAFT→EDITING→LOCKED→ARCHIVED) can be extended with new states without modifying existing service logic |
| 3 | **DIP** — Dependency Inversion | `CollaborationService` and `WebSocketController` depend on the `CollaborationFacade` **interface/abstraction**, not on concrete service classes directly |
| 4 | **LSP** — Liskov Substitution | `ADMIN`, `EDITOR`, `VIEWER` roles are substitutable wherever a `User` is expected in `authorize()` — no role breaks the contract |

### GRASP Principles

| Member | Principle | How It's Applied |
|--------|-----------|------------------|
| 1 | **Creator** | `UserFactory` is responsible for creating `User` objects — it aggregates all the info needed (username, email, password, role) to construct them, following GRASP Creator |
| 2 | **Information Expert** | `Document` owns its own `version` counter and `status` — so it alone handles `updateContent()` and state transitions. The class with the data holds the responsibility. |
| 3 | **Indirection** | `CollaborationFacade` introduces an indirection layer between `WebSocketController` and the multiple underlying services (`CollaborationService`, `DocumentService`, `ActivityTracker`), reducing direct coupling |
| 4 | **Pure Fabrication** | `ActivityTracker` does not represent any real-world domain concept — it's an invented class created purely to achieve low coupling and high cohesion for logging/observing system events |

---

## 👤 Member 1 — Authentication & User Management

**Design Pattern Owned:** 🏭 **Creational → Factory Method**  
Implement a `UserFactory` or `DocumentFactory` that centralizes object creation logic.

### Major Use Case: User Login & Role-Based Authorization
> UC-01: A user enters credentials → system authenticates → grants access based on role (ADMIN / EDITOR / VIEWER)

**Full Stack Ownership:**
- **Model:** `User.java`, `Role.java`
- **Repository:** `UserRepository.java`
- **Service:** `AuthService.java` (`authenticate()`, `authorize()`)
- **Controller:** `AuthRestController.java` (`POST /api/auth/login`)
- **UI:** Login page (username/password form, role-aware redirect)

**Files to Create/Enhance:**
```
+ model/UserFactory.java          ← Factory Method pattern
+ controller/UserRestController.java  ← GET /api/users (admin only)
~ service/AuthService.java        ← Add register() method
~ frontend: login.html / app.js   ← Login form UI
```

### Minor Use Case: User Registration
> UC-05: A new user self-registers with a username, email, password → assigned EDITOR role by default

**Endpoints:**
- `POST /api/auth/register`

---

## 👤 Member 2 — Document Lifecycle Management

**Design Pattern Owned:** 🏛️ (Supports overall MVC structure — focuses on state machine clarity)  
Document state transitions: DRAFT → EDITING → LOCKED → ARCHIVED

### Major Use Case: Create, View & Delete Documents
> UC-02: An authenticated user creates a document → it appears in the document list → owner/admin can delete it

**Full Stack Ownership:**
- **Model:** `Document.java`, `DocumentStatus.java`
- **Repository:** `DocumentRepository.java`
- **Service:** `DocumentService.java` (`createDocument()`, `getAllDocuments()`, `deleteDocument()`, `archiveDocument()`)
- **Controller:** `DocumentRestController.java` (`POST`, `GET`, `DELETE /api/documents`)
- **UI:** Document dashboard (list view, create form, delete button)

**Files to Create/Enhance:**
```
~ model/Document.java             ← Add tags/description field
~ service/DocumentService.java    ← Add search/filter by title
+ controller endpoint: GET /api/documents/search?title=
~ frontend: dashboard.html        ← Document list, create form, delete button
```

### Minor Use Case: Archive a Document (Admin Only)
> UC-06: An admin archives a document → it moves to ARCHIVED status → hidden from regular users

**Endpoints:**
- `PATCH /api/documents/{id}/archive`

---

## 👤 Member 3 — Real-time Collaboration & Conflict Resolution

**Design Pattern Owned:** 🏗️ **Structural → Facade Pattern**  
Implement `CollaborationFacade.java` that provides a clean API to the WebSocket controller, hiding the complexity of interacting with multiple services.

### Major Use Case: Real-Time Document Editing with Conflict Resolution
> UC-03: Multiple users edit the same document simultaneously → system detects version conflicts → auto-resolves via Last-Writer-Wins → all clients receive synced content

**Full Stack Ownership:**
- **Model:** `EditOperation.java`
- **Repository:** `EditOperationRepository.java`
- **Service:** `CollaborationService.java` (`applyEdit()`, `resolveConflict()`)
- **Facade:** `CollaborationFacade.java` ← **new, Facade Pattern**
- **Controller:** `WebSocketController.java` (`/document.edit`, `/document.delta`, `/document.register`)
- **UI:** Real-time editor (Quill.js), conflict notification toast, version badge

**Files to Create/Enhance:**
```
+ service/CollaborationFacade.java   ← Structural: Facade pattern
~ service/CollaborationService.java  ← Refine conflict strategy
~ controller/WebSocketController.java ← Use Facade instead of direct services
~ frontend: editor.html / editor.js  ← Quill.js editor, live sync
```

### Minor Use Case: Live Cursor Sharing
> UC-07: Each collaborator's cursor position is broadcast to all others in real-time with a unique color label

**WebSocket:** `/document.cursor/{documentId}` → `/topic/document.cursor/{documentId}`

---

## 👤 Member 4 — Activity Tracking & Admin Dashboard

**Design Pattern Owned:** 👁️ **Behavioral → Observer Pattern**  
`ActivityTracker` acts as an Observer — services (`AuthService`, `DocumentService`, `CollaborationService`) all `notify` it when key events occur (login, edit, create, archive).

### Major Use Case: Activity Log & Metrics Dashboard
> UC-04: An admin opens the dashboard → sees all system events (logins, edits, creates) → views counts and metrics per user/document

**Full Stack Ownership:**
- **Model:** `ActivityEvent.java`
- **Repository:** `ActivityEventRepository.java`
- **Service:** `ActivityTracker.java` (`logEvent()`, `generateMetrics()`)
- **Controller:** New `AdminRestController.java`
  - `GET /api/admin/activity` → returns all events
  - `GET /api/admin/metrics` → returns aggregated stats
- **UI:** Admin dashboard (event table, metrics cards)

**Files to Create/Enhance:**
```
+ controller/AdminRestController.java   ← Admin-only endpoints
~ service/ActivityTracker.java          ← Add perUser/perDocument metrics
~ model/ActivityEvent.java              ← Add metadata fields if needed
+ frontend: admin.html                  ← Event log table + metrics cards
```

### Minor Use Case: Edit History for a Document
> UC-08: A user opens a document → clicks "History" → sees all past edits with timestamps and who made them

**Endpoints:**
- `GET /api/documents/{id}/history` → returns list of `EditOperation` records

---

## 📊 Summary Table

| Member | Major Use Case | Minor Use Case | Design Pattern | SOLID | GRASP |
|--------|---------------|----------------|----------------|-------|-------|
| **1** | User Login + Role Auth | User Registration | Factory Method (Creational) | SRP | Creator |
| **2** | Create / View / Delete Docs | Archive Document | MVC (Framework) | OCP | Information Expert |
| **3** | Real-time Edit + Conflict Resolution | Live Cursor Sharing | Facade (Structural) | DIP | Indirection |
| **4** | Activity Log + Metrics | Edit History | Observer (Behavioral) | LSP | Pure Fabrication |

---

## 🗂️ File Ownership Map

```
auth/
  AuthRestController.java       → Member 1
  AuthService.java              → Member 1
  UserFactory.java              → Member 1 (NEW)
  UserRestController.java       → Member 1 (NEW)

document/
  DocumentRestController.java   → Member 2
  DocumentService.java          → Member 2
  Document.java                 → Member 2
  DocumentStatus.java           → Member 2

collaboration/
  WebSocketController.java      → Member 3
  CollaborationService.java     → Member 3
  CollaborationFacade.java      → Member 3 (NEW)
  EditOperation.java            → Member 3

admin/
  AdminRestController.java      → Member 4 (NEW)
  ActivityTracker.java          → Member 4
  ActivityEvent.java            → Member 4

config/
  DataSeeder.java               → Shared / Member 1 leads
  WebSocketConfig.java          → Member 3

frontend/
  login.html                    → Member 1
  dashboard.html                → Member 2
  editor.html                   → Member 3
  admin.html                    → Member 4
```

---

> [!IMPORTANT]
> Every member must implement their use case **end-to-end**: Model → Repository → Service → Controller → Frontend UI. Avoid splitting UI and Backend across members for the same use case.

> [!TIP]
> For the viva, each member should be able to demo their use case independently and explain exactly which design pattern/principle they implemented and *why*.
