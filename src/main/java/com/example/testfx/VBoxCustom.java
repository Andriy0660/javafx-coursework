package com.example.testfx;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class VBoxCustom extends VBox {
    private final int id;
    private final Song song;

    public VBoxCustom(int id, Song song) {
        super();
        this.id = id;
        this.song = song;
    }

    public int getCustomId() {
        return id;
    }

    public Song getSong() {
        return song;
    }
}
