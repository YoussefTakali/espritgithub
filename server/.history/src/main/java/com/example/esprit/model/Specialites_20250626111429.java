package com.example.esprit.model;

public enum Specialites {
    GL("Génie Logiciel"),
    IA("Intelligence Artificielle"), 
    DS("Data Science"),
    IOT("Internet of Things"),
    TRANC_COMMUN("Tronc Commun"),
    CYBER_SECURITE("Cyber Sécurité"),
    RESEAUX_TELECOM("Réseaux et Télécommunications"),
    DEVELOPPEMENT_WEB("Développement Web"),
    MOBILE_DEV("Développement Mobile"),
    CLOUD_COMPUTING("Cloud Computing");

    private final String displayName;

    Specialites(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Specialites fromString(String text) {
        for (Specialites specialite : Specialites.values()) {
            if (specialite.name().equalsIgnoreCase(text) || 
                specialite.displayName.equalsIgnoreCase(text)) {
                return specialite;
            }
        }
        // Default fallback
        return TRANC_COMMUN;
    }
}
