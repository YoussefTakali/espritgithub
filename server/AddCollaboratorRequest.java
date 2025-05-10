package com.example.gitbackend.entities;

public class AddCollaboratorRequest {


        private String owner;
        private String repo;
        private String username;
        private String permission;// e.g. "push", "pull", "admin"
        // getters and setters

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getRepo() {return repo;}
    public void setRepo(String repo) {this.repo = repo;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPermission() {return permission;}

    public void setPermission(String permission) {
        this.permission = permission;
    }
}

