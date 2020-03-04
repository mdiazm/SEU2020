package com.seu.sensors;

public class Sensor {

    private String name;
    private boolean state;
    private int image;

    public Sensor(String name, boolean state, int image){
        this.name = name;
        this.state = state;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }
}
