package esprit.git.gestionsummary_backgit_me.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Annee {
    @JsonProperty("PREMIERE_ANNEE")
    PREMIERE_ANNEE,
    @JsonProperty("DEUXIEME_ANNEE")
        DEUXIEME_ANNEE,
    @JsonProperty("TROISIEME_ANNEE")
        TROISIEME_ANNEE,
    @JsonProperty("QUATRIEME_ANNEE")
        QUATRIEME_ANNEE
    }