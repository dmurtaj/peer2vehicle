package ch.zhaw.peer2vehicle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
    private String to;
    private String subject;
    private String message;
}