package ru.formatc.yagpt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dialog")
@Getter
@Setter
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "username", length = 64, nullable = false)
    private String userName;

    @Column(name = "contextId", nullable = false)
    private long contextId;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "isNewContext", nullable = false)
    private boolean isNewContext = false;
}