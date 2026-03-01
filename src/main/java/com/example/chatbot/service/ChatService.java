package com.example.chatbot.service;

import com.example.chatbot.model.Conversation;
import com.example.chatbot.model.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Simple service that manages conversations in memory and provides mock responses.
 * In a real app, this would call an AI API.
 */
public class ChatService {
    // ================= IN-MEMORY STORE =================
    private final List<Conversation> conversations = new ArrayList<>();
    private static final int TITLE_MAX_LENGTH = 28;
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "from", "how", "i", "in", "is", "it", "me", "my",
            "of", "on", "or", "please", "so", "that", "the", "this",
            "to", "we", "what", "when", "where", "which", "who", "why",
            "with", "you", "your", "about", "can", "could", "would", "should"
    ));

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
        if (shouldAutoRenameConversation(conv)) {
            conv.setTitle(buildTitleFromUserText(text));
            conv.setTitleFinalized(true);
        }

        // ---- Append User Message ----
        Message userMsg = new Message(Message.Sender.USER, text);
        conv.addMessage(userMsg);

        // ---- Append Mock Bot Response ----
        Message botMsg = new Message(Message.Sender.BOT, "Echo: " + text);
        conv.addMessage(botMsg);
        return botMsg;
    }

    private boolean shouldAutoRenameConversation(Conversation conv) {
        if (conv.isTitleFinalized()) {
            return false;
        }
        String currentTitle = conv.getTitle();
        return currentTitle == null || currentTitle.isBlank() || "New Chat".equalsIgnoreCase(currentTitle.trim());
    }

    private String buildTitleFromUserText(String text) {
        if (text == null) {
            return "New Chat";
        }
        String normalized = text.replaceAll("https?://\\S+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.isEmpty()) {
            return "New Chat";
        }

        String inferred = inferTopicTitle(normalized);
        if (inferred.length() <= TITLE_MAX_LENGTH) {
            return inferred;
        }
        return inferred.substring(0, TITLE_MAX_LENGTH - 3).trim() + "...";
    }

    private String inferTopicTitle(String normalized) {
        String[] words = normalized.replaceAll("[^A-Za-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .split(" ");

        List<String> keywords = new ArrayList<>();
        for (String rawWord : words) {
            if (rawWord.isBlank()) {
                continue;
            }
            String lower = rawWord.toLowerCase(Locale.ROOT);
            if (lower.length() < 3 || STOP_WORDS.contains(lower)) {
                continue;
            }
            keywords.add(toTitleCase(lower));
            if (keywords.size() == 4) {
                break;
            }
        }

        if (!keywords.isEmpty()) {
            return String.join(" ", keywords);
        }

        String fallback = normalized;
        int questionMark = fallback.indexOf('?');
        if (questionMark > 0) {
            fallback = fallback.substring(0, questionMark);
        }
        fallback = fallback.trim();
        if (fallback.isEmpty()) {
            return "New Chat";
        }
        String[] fallbackWords = fallback.split(" ");
        int take = Math.min(4, fallbackWords.length);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < take; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(toTitleCase(fallbackWords[i].toLowerCase(Locale.ROOT)));
        }
        return builder.toString();
    }

    private String toTitleCase(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
