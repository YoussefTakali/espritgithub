package com.example.gitbackend.entities;

public class CreateRepoRequest {

        private String name;
        private String ownerName;
        private String description;
        private boolean isPrivate;
        private boolean auto_init; // <-- matches JSON
        private String gitignore_template; // <-- matches JSON

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPrivate() {
            return isPrivate;
        }

        public void setPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public boolean isAuto_init() {
            return auto_init;
        }

        public void setAuto_init(boolean auto_init) {
            this.auto_init = auto_init;
        }

        public String getGitignore_template() {
            return gitignore_template;
        }

        public void setGitignore_template(String gitignore_template) {
            this.gitignore_template = gitignore_template;
        }
        public String getOwnerName() {
            return ownerName;
        }
        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }
    }
