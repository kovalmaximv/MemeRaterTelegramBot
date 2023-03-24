package ru.mkskoval.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemeID implements Serializable {
    private Long messageId;
    private Long chatId;
}
