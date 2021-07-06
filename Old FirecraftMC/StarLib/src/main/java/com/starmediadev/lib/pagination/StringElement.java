package com.starmediadev.lib.pagination;

public class StringElement implements IElement {
    
    private String string;
    public StringElement(String string) {
        this.string = string;
    }
    
    public String formatLine(String... args) {
        return string;
    }
}