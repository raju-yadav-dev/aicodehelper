package com.example.chatbot.model;

import java.time.LocalDateTime;

public class Message {
    // ================= MESSAGE SENDER =================
    public enum Sender { USER, BOT }

    // ================= DATA =================
    private final Sender sender;
    private final String content;
    private final LocalDateTime timestamp;

    // ================= CONSTRUCTOR =================
    public Message(Sender sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // ================= ACCESSORS =================
    public Sender getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
