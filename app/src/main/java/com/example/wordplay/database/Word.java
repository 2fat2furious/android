package com.example.wordplay.database;

public class Word {
    private String language;
    private String word;

    public Word(String word, String language){
        this.word = word;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
