package com.anish.calabashbros;

public class Thing {

    protected World world;

    public Tile<? extends Thing> tile;

    protected enum Position {
        WALL, FLOOR, BEAN, CHERRY, DRUG, CALABASH, MONSTER, BULLET
    }

    public int getX() {
        return this.tile.getxPos();
    }

    public int getY() {
        return this.tile.getyPos();
    }

    public void setTile(Tile<? extends Thing> tile) {
        this.tile = tile;
    }

    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {        
        this.url = url; 
    }

    Thing(String url, World world) {        
        setUrl(url);          
        this.world = world;
    }

}
