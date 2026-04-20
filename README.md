# Collaborative Editor Application

A real-time collaborative document editing platform built with Spring Boot and WebSocket technology. Multiple users can edit documents simultaneously with real-time synchronization, activity tracking, and role-based access control.

## Features

- **Real-time Collaboration**: Multiple users can edit the same document simultaneously with live updates
- **Activity Tracking**: Every edit operation is logged with user information and timestamps
- **User Authentication**: Secure user registration and login system
- **Role-Based Access Control**: Support for different user roles (Admin, Editor, Viewer)
- **Document Management**: Create, update, and manage documents with status tracking
- **WebSocket Communication**: Bidirectional communication for instant synchronization
- **Operational Transformation**: Edit operations are tracked and stored for conflict resolution

## Technology Stack

- **Backend**: Java, Spring Boot, Spring Data JPA
- **Database**: MySQL/H2 (configured in properties)
- **Communication**: WebSocket (STOMP)
- **Frontend**: HTML5, CSS3, JavaScript
- **Build Tool**: Maven

## Project Structure

```
src/main/java/com/ooad/project/collabeditor/
├── CollaborativeEditorApplication.java    # Main Spring Boot application
├── config/                                 # Configuration classes
│   ├── DataSeeder.java                    # Initial data setup
│   └── WebSocketConfig.java               # WebSocket configuration
├── controller/                             # REST and WebSocket controllers
│   ├── AuthRestController.java            # Authentication endpoints
│   ├── DocumentRestController.java        # Document management endpoints
│   └── WebSocketController.java           # WebSocket message handlers
├── model/                                  # JPA entities
│   ├── ActivityEvent.java                 # Activity log entries
│   ├── Document.java                      # Document entity
│   ├── DocumentStatus.java                # Document status enum
│   ├── EditOperation.java                 # Edit operation entity
│   ├── Role.java                          # User role enum
│   └── User.java                          # User entity
├── repository/                             # Spring Data repositories
│   ├── ActivityEventRepository.java
│   ├── DocumentRepository.java
│   ├── EditOperationRepository.java
│   └── UserRepository.java
└── service/                                # Business logic services
    ├── ActivityTracker.java               # Activity tracking service
    ├── AuthService.java                   # Authentication service
    ├── CollaborationService.java          # Collaboration logic
    └── DocumentService.java               # Document operations

src/main/resources/
├── application.properties                  # Application configuration
└── static/                                # Frontend files
    ├── index.html                         # Main HTML page
    ├── css/style.css                      # Styling
    └── js/app.js                          # Client-side logic
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MySQL 5.7+ (or H2 for embedded database)
- Modern web browser with WebSocket support

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd DocHub
   ```


2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   or
   ```bash
   java -jar target/collabeditor-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application**
   - Open your browser and navigate to `http://localhost:8080`

## API Documentation

### Authentication Endpoints

- `POST /api/auth/register` - Register a new user
  - Request body: `{username, password, email}`
  
- `POST /api/auth/login` - User login
  - Request body: `{username, password}`
  - Returns: JWT token

### Document Endpoints

- `GET /api/documents` - Get all documents
- `GET /api/documents/{id}` - Get specific document
- `POST /api/documents` - Create new document
  - Request body: `{title, content}`
- `PUT /api/documents/{id}` - Update document
  - Request body: `{title, content, status}`
- `DELETE /api/documents/{id}` - Delete document

### WebSocket Events

- **CONNECT**: Establish WebSocket connection
- **SEND**: Send edit operation to collaborators
- **RECEIVE**: Receive edit updates from other users
- **DISCONNECT**: Close WebSocket connection

## Architecture Overview

### Real-time Synchronization Flow

1. User edits document in the browser
2. Client captures the edit operation
3. Edit is sent via WebSocket to the server
4. Server stores the operation in database
5. Server broadcasts the operation to all connected users
6. All clients receive and apply the operation in real-time

### Activity Tracking

- Every edit operation is logged as an `ActivityEvent`
- Each activity event records:
  - User who made the edit
  - Document affected
  - Type of operation (INSERT, DELETE, UPDATE)
  - Timestamp
  - Content of the edit

## Running Tests

```bash
mvn test
```

## Future Enhancements

- Rich text editor support
- Document versioning and history rollback
- Comments and discussions
- Offline mode with sync
- Document sharing with granular permissions
- Real-time cursor position tracking

## License

This project is licensed under the MIT License.

## Support

For issues or feature requests, please open an issue in the repository.
