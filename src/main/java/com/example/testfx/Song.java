package com.example.testfx;

import java.util.Arrays;

public class Song {
    private String authorName;
    private String songName;
    private Couplet[] couplets;
    private String mp3;

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public Song() {
    }

    public Song(String authorName, String songName, Couplet[] couplets) {
        this.authorName = authorName;
        this.songName = songName;
        this.couplets = couplets;
    }

    @Override
    public String toString() {
        return "Song{" +
                "authorName='" + authorName + '\'' +
                ", songName='" + songName + '\'' +
                ", couplets=" + Arrays.toString(couplets) +
                '}';
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Couplet[] getCouplets() {
        return couplets;
    }

    public void setCouplets(Couplet[] couplets) {
        this.couplets = couplets;
    }
    static class Couplet{
        private int id;
        private String coupletString;

        public Couplet() {
        }

        public Couplet(int id, String coupletString) {
            this.id = id;
            this.coupletString = coupletString;
        }

        @Override
        public String toString() {
            return coupletString;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCoupletString() {
            return coupletString;
        }

        public void setCoupletString(String coupletString) {
            this.coupletString = coupletString;
        }
    }
}
