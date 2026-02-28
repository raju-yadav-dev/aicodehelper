package com.aicodehelper.controller;

import com.aicodehelper.model.Conversation;
import com.aicodehelper.model.Message;
import com.aicodehelper.service.ChatService;
import com.aicodehelper.service.ConversationManager;
import com.aicodehelper.util.AppConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ChatController acts as the bridge between UI layer and business logic.
 *
 * Responsibilities:
 * - Manage conversation lifecycle (create, switch, delete)
 * - Delegate message generation to ChatService
 * - Maintain conversation state through ConversationManager
 * - Provide conversation history for UI display
 *
 * Architecture: Follows MVP pattern where:
 * - View: MainLayout, ChatView, SidebarView
 * - Controller: This class (ChatController)
 * - Model: Message, Conversation
 * - Service: ChatService, ConversationManager
 *
 * This separation ensures:
 * - Easy unit testing (services can be mocked)
 * - Clean dependency injection path
 * - Clear separation of concerns
 * - Reusability of business logic
 */
public class ChatController {
    private final ChatService chatService;
    private final ConversationManager conversationManager;

    /**
     * Constructor initializing services.
     * Services can be injected here for testing/dependency injection.
     */
    public ChatController() {
        this.chatService = new ChatService();
        this.conversationManager = new ConversationManager();
    }

    /**
     * Constructor for dependency injection (useful for testing).
     * @param chatService The AI response service
     * @param conversationManager The conversation manager service
     */
    public ChatController(ChatService chatService, ConversationManager conversationManager) {
        this.chatService = chatService;
        this.conversationManager = conversationManager;
    }

    // ========== CONVERSATION MANAGEMENT ==========

    /**
     * Creates a new conversation and sets it as current.
     * Called when user clicks "New Chat" button.
     */
    public void startNewChat() {
        String title = AppConfig.NEW_CHAT_DEFAULT_TITLE;
        conversationManager.createNewConversation(title);
    }

    /**
     * Switches to an existing conversation by index.
     * @param index Position in the conversation list
     */
    public void switchToConversation(int index) {
        List<Conversation> conversations = conversationManager.getAllConversations();
        if (index >= 0 && index < conversations.size()) {
            conversationManager.setCurrentConversation(conversations.get(index));
        }
    }

    /**
     * Gets the currently active conversation.
     * @return Optional containing the current conversation
     */
    public Optional<Conversation> getCurrentConversation() {
        return conversationManager.getCurrentConversation();
    }

    /**
     * Deletes a conversation from history.
     * @param index Position in the conversation list
     */
    public void deleteConversation(int index) {
        List<Conversation> conversations = conversationManager.getAllConversations();
        if (index >= 0 && index < conversations.size()) {
            conversationManager.deleteConversation(conversations.get(index));
        }
    }

    // ========== MESSAGE MANAGEMENT ==========

    /**
     * Creates a user message and adds it to the current conversation.
     * Also updates the conversation title if it's the first message.
     * @param input The user's message text
     * @return The created user message
     */
    public Message createUserMessage(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(AppConfig.EMPTY_INPUT_WARNING);
        }

        Message userMessage = new Message(Message.Sender.USER, input.trim(), LocalDateTime.now(), false);
        
        Optional<Conversation> current = getCurrentConversation();
        if (current.isEmpty()) {
            startNewChat();
            current = getCurrentConversation();
        }

        current.ifPresent(conversation -> {
            conversation.addMessage(userMessage);
        });

        return userMessage;
    }

    /**
     * Generates an AI response and adds it to the current conversation.
     * Uses ChatService to generate context-aware responses.
     * @param userInput The original user input (for context)
     * @return The generated bot message
     */
    public Message createBotReply(String userInput) {
        Message botMessage = chatService.generateResponse(userInput);
        
        Optional<Conversation> current = getCurrentConversation();
        current.ifPresent(conversation -> {
            conversation.addMessage(botMessage);
        });

        return botMessage;
    }

    /**
     * Gets all messages from the current conversation.
     * @return List of messages in conversation order
     */
    public List<Message> getCurrentMessages() {
        return getCurrentConversation()
                .map(Conversation::getMessages)
                .orElse(List.of());
    }

    // ========== HISTORY & DISPLAY ==========

    /**
     * Gets conversation titles for the sidebar history list.
     * Returns titles in reverse chronological order (most recent first).
     * @return List of conversation titles
     */
    public List<String> getChatHistory() {
        return conversationManager.getConversationTitles();
    }

    /**
     * Gets the number of conversations.
     * @return conversation count
     */
    public int getConversationCount() {
        return conversationManager.getConversationCount();
    }

    /**
     * Clears all conversations (usually on app shutdown or reset).
     * WARNING: This action is not reversible unless persistence is implemented.
     */
    public void clearAllConversations() {
        conversationManager.clear();
    }

    /**
     * For backward compatibility with existing UI code.
     * Returns messages from current conversation.
     * @return list of messages
     */
    public List<Message> getMessages() {
        return getCurrentMessages();
    }
}
