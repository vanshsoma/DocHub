# Collaborative Editor Application

A real-time collaborative document editing platform built with Spring Boot and WebSocket technology. Multiple users can edit documents simultaneously with real-time synchronization, activity tracking, and role-based access control.

## Problem Statement

In today's fast-paced environment, teams need to work seamlessly on shared documents without worrying about version conflicts, manual merging, or overwriting each other's changes. The objective of DOCHUB is to provide a reliable, real-time collaborative editing platform that allows multiple users to edit the same document concurrently with low latency, robust conflict resolution, and secure role-based access.

## Key Features

- **Real-time Collaboration**: Multiple users can edit the same document simultaneously with live updates, utilizing collaborative cursors.
- **Rich Text Editing**: Integrated with Quill.js for a full-featured rich-text editing experience.
- **Conflict Resolution**: Robust synchronization handling to prevent infinite edit loops and ensure data consistency.
- **Activity Tracking**: Every edit operation is logged with user information and timestamps.
- **User Authentication**: Secure user registration and login system with JWT tokens.
- **Role-Based Access Control**: Support for different user roles (Admin, Editor, Viewer).
- **Document Management**: Create, update, delete, and manage documents with status tracking.
- **WebSocket Communication**: Bidirectional communication via STOMP for instant synchronization.

## Architecture and Design

### MVC Architecture Used? 
**Yes.** The application follows the traditional Model-View-Controller (MVC) architectural pattern:
- **Model**: JPA Entities (`Document`, `User`, `EditOperation`, `ActivityEvent`) represent the application's data layer.
- **View**: The frontend built with HTML5, CSS3, JavaScript, and Quill.js (located in `src/main/resources/static/`) serves as the client-side view.
- **Controller**: Spring REST and WebSocket Controllers (`DocumentRestController`, `WebSocketController`, `AuthRestController`) handle client requests, invoke business logic, and route data back to the view.

### Design Principles

- **Single Responsibility Principle (SRP)**: Each class is designed to handle a specific functionality. For instance, `AuthService` handles only authentication, while `CollaborationService` strictly manages real-time synchronization logic.
- **Separation of Concerns (SoC)**: The project structure cleanly separates the application into distinct layers: controllers (routing & web layer), services (business logic), repositories (data access), and configuration. 
- **Dependency Inversion Principle (DIP)**: Realized through Spring's Dependency Injection (DI). Controllers depend on injected service beans rather than hardcoded implementations, allowing for loose coupling.

### Design Patterns

- **Repository Pattern**: Extensively used via Spring Data JPA (`DocumentRepository`, `UserRepository`, etc.) to abstract underlying database operations and provide a clean, object-oriented data access layer.
- **Publisher-Subscriber (Observer) Pattern**: Implemented using STOMP over WebSockets. Clients subscribe to specific topics (e.g., a specific document's sync channel) and the server publishes edit events to these topics to visually notify all observing clients.
- **Singleton Pattern**: Spring globally manages all components (`@Service`, `@Controller`, `@Repository`) as Singletons by default, ensuring only one instance of each service is created, cached, and shared across the entire application runtime.
- **Facade Pattern**: Service classes (e.g., `CollaborationService`) act as a unified, simplified interface to the complex subsystems below them, hiding the complexity of simultaneously saving to the database, tracking activities, and broadcasting over WebSockets.

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

## Individual Contributions

The project development was divided among four team members, balancing the architectural, synchronization, security, and data layers:

1. **Member 1: Backend Architecture & Real-time Synchronization** 
   - Configured the WebSocket (STOMP) server and message broker routing.
   - Implemented the `CollaborationService` to handle incoming edit operations and broadcast them efficiently to all connected clients.
   - Handled server-side conflict resolution and concurrency flow logic.

2. **Member 2: Frontend Engineering & UI Integration** 
   - Developed the HTML5/CSS3 frontend web interface and fully integrated the `Quill.js` rich text editor.
   - Implemented client-side WebSocket communication logic in Javascript to listen for and render dynamic document updates.
   - Designed collaborative visual markers like colored cursors to show active user interactions in the DOM.

3. **Member 3: Security & Identity Management** 
   - Built the `AuthRestController` and implemented the JWT-based authentication and secure session flow.
   - Designed the `User` and `Role` entities alongside password encryption mechanisms.
   - Implemented Role-Based Access Control to ensure sensitive endpoints and documents are securely gatekept.

4. **Member 4: Database Design & Core Document APIs** 
   - Modeled the core data layer creating JPA models (`Document`, `ActivityEvent`, `EditOperation`) and Spring Repositories.
   - Developed the `DocumentRestController` to expose standard REST APIs (CRUD operations) for managing documents.
   - Engineered the `ActivityTracker` service to systematically log and retrieve all edit operations made by users.

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
