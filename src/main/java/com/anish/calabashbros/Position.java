package com.anish.calabashbros;

public enum Position {
    WALL("wall"),
    FLOOR("floor"), 
    BEAN("bean"), 
    CHERRY("cherry"),
    DRUG("drug"), 
    CALABASH("calabash"), 
    MONSTER("monster"), 
    BULLET("bullet");

    private String id;
    private static final long serialVersionUID = 11L;

    Position(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static Position getElementById(String id) {
        if (id == "wall") {
            return WALL;
        }
        else if (id == "bean") {
            return BEAN;
        }
        else if (id == "cherry") {
            return CHERRY;
        }
        else if (id == "drug") {
            return DRUG;
        }
        else if (id == "calabash") {
            return CALABASH;
        }
        else if (id == "monster") {
            return MONSTER;
        }
        else if (id == "bullet") {
            return BULLET;
        }
        else { //if (id == "FLOOR")
            return FLOOR;
        }
    }
}
