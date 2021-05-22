package com.abhishek.notesapp;

public class model {
    private String title;
    private String content;

    public model() {
    }

    public model(String title, String content){
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }



}
