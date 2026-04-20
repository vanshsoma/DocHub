package com.ooad.project.collabeditor.model;

/**
 * Factory Method Pattern (Creational)
 *
 * UserFactory centralises the creation of User objects.
 * Instead of callers using `new User(...)` directly and having to know
 * which Role to assign, they call a named factory method that encapsulates
 * the creation logic and enforces correct default values per role.
 *
 * Factory methods:
 *  - createAdmin()  → Role.ADMIN
 *  - createEditor() → Role.EDITOR  (default for self-registered users)
 *  - createViewer() → Role.VIEWER
 */
public class UserFactory {

    /**
     * Creates a User with ADMIN role.
     * Admins can manage all documents and view system metrics.
     */
    public static User createAdmin(String username, String email, String password) {
        return new User(username, email, password, Role.ADMIN);
    }

    /**
     * Creates a User with EDITOR role.
     * Editors can create, read, and edit documents.
     * This is the default role for self-registered users.
     */
    public static User createEditor(String username, String email, String password) {
        return new User(username, email, password, Role.EDITOR);
    }

    /**
     * Creates a User with VIEWER role.
     * Viewers can only read documents — no editing allowed.
     */
    public static User createViewer(String username, String email, String password) {
        return new User(username, email, password, Role.VIEWER);
    }
}
