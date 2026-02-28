package com.aicodehelper.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a conversation session with a unique ID, title, and list of messages.
 * This model manages the conversation state immutably at the collection level.
 * Production-ready with proper encapsulation and documentation.
 */
public class Conversation {
    private final String conversationId;
    private final String title;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<Message> messages;

    /**
     * Creates a new conversation with auto-generated ID and title.
     * @param title The conversation title (typically generated from first user message)
     */
    public Conversation(String title) {
        this(UUID.randomUUID().toString(), title, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
    }

    /**
     * Creates a conversation with specified ID and timestamps.
     * @param conversationId Unique identifier for this conversation
     * @param title Display title for the conversation
     * @param createdAt When this conversation was created
     * @param updatedAt Last update timestamp
     * @param messages Initial list of messages (can be empty)
     */
    public Conversation(String conversationId, String title, LocalDateTime createdAt, LocalDateTime updatedAt, List<Message> messages) {
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
        this.messages = new ArrayList<>(Objects.requireNonNull(messages, "messages cannot be null"));
    }

    // ========== PUBLIC API ==========

    /**
     * Adds a message to this conversation and updates the timestamp.
     * @param message The message to add
     */
    public void addMessage(Message message) {
        Objects.requireNonNull(message, "message cannot be null");
        this.messages.add(message);
    }

    /**
     * Returns an unmodifiable view of the messages to prevent external mutation.
     * @return immutable list of messages
     */
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Returns message count for this conversation.
     * @return number of messages
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Checks if this conversation has messages.
     * @return true if conversation contains at least one message
     */
    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    /**
     * Clears all messages from this conversation.
     * Use with caution - this operation is NOT reversible.
     */
    public void clearMessages() {
        messages.clear();
    }

    // ========== GETTERS ==========

    public String getConversationId() {
        return conversationId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ========== OBJECT CONTRACT ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(conversationId, that.conversationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id='" + conversationId + '\'' +
                ", title='" + title + '\'' +
                ", messages=" + messages.size() +
                ", created=" + createdAt +
                '}';
    }
}
