package ru.mkskoval.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemeScoreID implements Serializable {
    private Meme meme;
    private Long userId;
}
