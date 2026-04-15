package com.ooad.project.collabeditor.config;

import com.ooad.project.collabeditor.model.Document;
import com.ooad.project.collabeditor.model.Role;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.DocumentRepository;
import com.ooad.project.collabeditor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User("admin", "admin@editor.com", "admin123", Role.ADMIN);
            User editor1 = new User("editor1", "editor1@editor.com", "pass", Role.EDITOR);
            User editor2 = new User("editor2", "editor2@editor.com", "pass", Role.EDITOR);
            User editor3 = new User("editor3", "editor3@editor.com", "pass", Role.EDITOR);

            userRepository.save(admin);
            userRepository.save(editor1);
            userRepository.save(editor2);
            userRepository.save(editor3);

            Document doc1 = new Document("Project Plan",
                    "This is the initial project plan for the Spring Boot application.", admin);
            documentRepository.save(doc1);

            System.out.println("Mock data seeded successfully.");
        }
    }
}
