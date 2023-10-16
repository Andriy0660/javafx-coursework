package com.example.testfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void insertWord(Song song) {
        String songName = song.getSongName();
        String[] words = songName.toLowerCase().split("\\s+"); // Розбиття рядку на слова, використовуючи пробіли як роздільник

        TrieNode current = root;
        for (String word : words) {
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
            current.listOfSongs.add(song); // Зберігайте інформацію про пісню в TrieNode
            current = root; // Почніть спочатку для наступного слова
        }
    }

    public void insert(String word) {
        TrieNode current = root;
        for (int i=0; i<word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                node = new TrieNode();
                current.children.put(ch, node);
            }
            current = node;
        }
        current.endOfString = true;
    }
    public boolean search(String word) {
        TrieNode currentNode = root;
        for (int i =0; i<word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = currentNode.children.get(ch);
            if (node == null) {
                return false;
            }
            currentNode = node;
        }
        return currentNode.endOfString;
    }
    public boolean delete(String word) {
        if (!search(word)) {
            return false;
        }
        delete(root, word, 0);
        return true;
    }

    private boolean delete(TrieNode node, String word, int index) {
        if (index == word.length()) {
            node.endOfString = false;
            return node.children.isEmpty(); // Повертаємо true, якщо вузол порожній
        }

        char ch = word.charAt(index);
        TrieNode childNode = node.children.get(ch);
        boolean shouldDeleteChild = delete(childNode, word, index + 1);
        if (shouldDeleteChild) {
            node.children.remove(ch);
            return node.children.isEmpty() && !node.endOfString;
        }
        return false;
    }
    public List<String> getAllWords() {
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        getAllWords(root, currentWord, words);
        return words;
    }

    private void getAllWords(TrieNode current, StringBuilder currentWord, List<String> words) {
        if (current.endOfString) {
            words.add(currentWord.toString());
        }

        for (char ch : current.children.keySet()) {
            TrieNode childNode = current.children.get(ch);
            currentWord.append(ch);
            getAllWords(childNode, currentWord, words);
            currentWord.deleteCharAt(currentWord.length() - 1);
        }
    }
    public List<Song> findWordsWithPrefix(String prefix) {
        List<Song> words = new ArrayList<>();
        TrieNode node = root;
        // Перейти до останнього вузла префікса
        for (char c : prefix.toCharArray()) {
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            } else {
                return words; // Префікс не знайдено
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
