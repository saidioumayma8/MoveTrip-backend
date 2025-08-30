package org.budgetmanager.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

        private static final long serialVersionUID = 1L; // For serialization

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false, length = 50)
        private String name; // e.g., "ROLE_USER", "ROLE_LOUER", "ROLE_ADMIN"

        // Constructors
        public Role() {
        }

        public Role(String name) {
            this.name = name;
        }


        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

}