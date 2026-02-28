package com.example.chatbot.controller;

import com.example.chatbot.model.Conversation;
import javafx.scene.control.ListCell;

/**
 * Simple cell that shows conversation title and optionally latest message.
 */
public class ConversationCell extends ListCell<Conversation> {
    // ================= CELL RENDER =================
    @Override
    protected void updateItem(Conversation item, boolean empty) {
        // ---- Clear Empty Rows ----
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            // ---- Render Conversation Title ----
            setText(item.getTitle());
        }
    }
}
