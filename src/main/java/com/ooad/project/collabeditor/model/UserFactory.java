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

    public static User createAdmin(String username, String email, String password) {
        return new User(username, email, password, Role.ADMIN);
    }


    public static User createEditor(String username, String email, String password) {
        return new User(username, email, password, Role.EDITOR);
    }

    public static User createViewer(String username, String email, String password) {
        return new User(username, email, password, Role.VIEWER);
    }
}
