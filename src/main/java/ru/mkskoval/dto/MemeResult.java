package ru.mkskoval.dto;

import lombok.Data;

@Data
public class MemeResult {
    private int likes;
    private int dislikes;
    private int accordions;

    public void increaseLikes() { likes++; }
    public void increaseDislikes() { dislikes++; }
    public void increaseAccordions() { accordions++; }
}
