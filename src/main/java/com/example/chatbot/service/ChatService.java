package com.example.chatbot.service;

import com.example.chatbot.model.Conversation;
import com.example.chatbot.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple service that manages conversations in memory and provides mock responses.
 * In a real app, this would call an AI API.
 */
public class ChatService {
    // ================= IN-MEMORY STORE =================
    private final List<Conversation> conversations = new ArrayList<>();

    // ================= CONVERSATION API =================
    public Conversation createConversation() {
        Conversation conv = new Conversation("New Chat");
        conversations.add(conv);
        return conv;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    // ================= MESSAGE API =================
    public Message sendMessage(Conversation conv, String text) {
        // ---- Append User Message ----
        Message userMsg = new Message(Message.Sender.USER, text);
        conv.addMessage(userMsg);

        // ---- Append Mock Bot Response ----
        Message botMsg = new Message(Message.Sender.BOT, "Echo: " + text);
        conv.addMessage(botMsg);
        return botMsg;
    }
}
