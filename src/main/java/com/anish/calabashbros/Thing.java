package com.anish.calabashbros;

import java.io.Serializable;

public class Thing implements Serializable {

    protected World world;
    private static final long serialVersionUID = 12L;

    public Tile<? extends Thing> tile;

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
