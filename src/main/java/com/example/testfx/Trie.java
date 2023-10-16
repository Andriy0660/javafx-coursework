package com.example.testfx;

import java.util.*;

public class Trie {
    static class TrieNode {
        Map<Character, TrieNode> children;
        boolean endOfString;
        List<Song> listOfSongs = new ArrayList<>();

        public TrieNode() {
            children = new HashMap<>();
            endOfString = false;
        }
    }
    private TrieNode root;
    public Trie() {
        root = new TrieNode();
    }
    public void insertSong(Song song) {
        List<String> allWordsToSave = new ArrayList<>();
        String songName = song.getSongName().toLowerCase();
        allWordsToSave.add(songName);
        String[] words = songName.split("\\s+");
        Collections.addAll(allWordsToSave, words);

        for(int i = 1; i < words.length - 1; i++) {
            StringBuilder word = new StringBuilder(words[i]);
            for(int j = i + 1; j < words.length; j++){
                word.append(" ").append(words[j]);
            }
            allWordsToSave.add(word.toString());
        }

        TrieNode current = root;
        for (String word : allWordsToSave) {
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);
                TrieNode node = current.children.get(ch);
                if (node == null) {
                    node = new TrieNode();
                    current.children.put(ch, node);
                }
                current = node;
            }
            current.endOfString = true;
            current.listOfSongs.add(song);
            current = root;
        }
    }

    public List<Song> findWordsWithPrefix(String prefix) {
        List<Song> words = new ArrayList<>();
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            } else {
                return words;
            }
        }
        findAllWordsWithPrefix(node, prefix, words);
        return words;
    }

    private void findAllWordsWithPrefix(TrieNode node, String currentPrefix, List<Song> words) {
        if (node.endOfString) {
            words.addAll(node.listOfSongs);
        }
        for (char c : node.children.keySet()) {
            findAllWordsWithPrefix(node.children.get(c), currentPrefix + c, words);
        }
    }
}
