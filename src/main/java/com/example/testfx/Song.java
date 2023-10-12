package com.example.testfx;

public class Song {
    static class Couplet{
        private int id;
        private String s;

        public Couplet() {
        }

        public Couplet(int id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }
    String authorName;
    String songName;
    Couplet[] couplets;

    public Song() {
    }

    public Song(String authorName, String songName, Couplet[] couplets) {
        this.authorName = authorName;
        this.songName = songName;
        this.couplets = couplets;
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
}
