package edu.du.garret.sugarbaker.primitives;

import java.io.Serializable;

public class Message implements Serializable {
    private final String owner, text;
    public Message(String owner, String text) {
        this.owner = owner;
        this.text = text;
    }

    @Override
    public String toString() {
        return owner + ": " + text;
    }
}
