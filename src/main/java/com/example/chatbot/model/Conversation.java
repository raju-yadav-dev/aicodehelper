package com.example.chatbot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Conversation {
    // ================= DATA =================
    private final String title;
    private final List<Message> messages = new ArrayList<>();

    // ================= CONSTRUCTOR =================
    public Conversation(String title) {
        this.title = title;
    }

    // ================= ACCESSORS =================
    public String getTitle() {
        return title;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    // ================= MUTATION =================
    public void addMessage(Message message) {
        messages.add(message);
    }
}
