package com.aicodehelper.service;

import com.aicodehelper.model.Conversation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Manages conversation lifecycle: creation, switching, deletion, and persistence.
 * This service provides a clean API for conversation operations and can be extended
 * to support database or file-based persistence.
 *
 * Thread Safety: Current implementation is thread-unsafe. For multi-threaded scenarios,
 * add synchronized blocks or use ConcurrentHashMap.
 *
 * Future Enhancement: Replace in-memory storage with:
 * - SQLite database for local persistence
 * - Cloud storage (Firebase, AWS DynamoDB)
 * - File-based JSON serialization
 */
public class ConversationManager {
    private final List<Conversation> conversations = new ArrayList<>();
    private Conversation currentConversation;

    /**
     * Creates a new conversation and sets it as current.
     * @param title The title for the new conversation
     * @return The newly created conversation
     */
    public Conversation createNewConversation(String title) {
        Conversation conversation = new Conversation(title);
        conversations.add(0, conversation); // Add to front of list
        setCurrentConversation(conversation);
        return conversation;
    }

    /**
     * Sets the currently active conversation.
     * @param conversation The conversation to activate
     */
    public void setCurrentConversation(Conversation conversation) {
        if (conversation != null && conversations.contains(conversation)) {
            this.currentConversation = conversation;
        }
    }

    /**
     * Retrieves the currently active conversation.
     * @return Optional containing the current conversation, or empty if none
     */
    public Optional<Conversation> getCurrentConversation() {
        return Optional.ofNullable(currentConversation);
    }

    /**
     * Gets an unmodifiable list of all conversations, most recent first.
     * @return all conversations in reverse chronological order
     */
    public List<Conversation> getAllConversations() {
        return Collections.unmodifiableList(conversations);
    }

    /**
     * Removes a conversation from the list.
     * If the deleted conversation was current, switches to the most recent one.
     * @param conversation The conversation to delete
     */
    public void deleteConversation(Conversation conversation) {
        if (conversations.remove(conversation)) {
            if (currentConversation == conversation) {
                currentConversation = conversations.isEmpty() ? null : conversations.get(0);
            }
        }
    }

    /**
     * Gets the count of conversations.
     * @return number of conversations
     */
    public int getConversationCount() {
        return conversations.size();
    }

    /**
     * Finds a conversation by ID.
     * @param conversationId The ID to search for
     * @return Optional containing the conversation if found
     */
    public Optional<Conversation> findById(String conversationId) {
        return conversations.stream()
                .filter(c -> c.getConversationId().equals(conversationId))
                .findFirst();
    }

    /**
     * Clears all conversations permanently.
     * WARNING: This operation cannot be undone if persistence is implemented.
     */
    public void clear() {
        conversations.clear();
        currentConversation = null;
    }

    /**
     * Returns conversation titles for UI display (sidebar history list).
     * @return list of titles in conversation order
     */
    public List<String> getConversationTitles() {
        return conversations.stream()
                .map(Conversation::getTitle)
                .toList();
    }
}
