package com.example.chatbot.controller;

import com.example.chatbot.model.Conversation;
import com.example.chatbot.model.Message;
import com.example.chatbot.service.ChatService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Objects;

public class ChatController {
    // ================= CHAT VIEW NODES =================
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messageBox;
    @FXML
    private TextArea inputArea;
    @FXML
    private Button sendButton;
    @FXML
    private HBox inputShell;

    // ================= STATE =================
    private Conversation conversation;
    private final ChatService chatService = new ChatService();
    private Runnable onConversationUpdated;

    // ================= INITIALIZATION =================
    @FXML
    public void initialize() {
        // ---- Disable Send For Empty Input ----
        sendButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> inputArea.getText() == null || inputArea.getText().trim().isEmpty(),
                        inputArea.textProperty()
                )
        );

        // ---- Send Actions ----
        sendButton.setOnAction(e -> sendMessage());

        // ---- Enter To Send, Shift+Enter For Newline ----
        inputArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                sendMessage();
                event.consume();
            }
        });

        inputArea.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                if (!inputShell.getStyleClass().contains("input-focused")) {
                    inputShell.getStyleClass().add("input-focused");
                }
            } else {
                inputShell.getStyleClass().remove("input-focused");
            }
        });
    }

    // ================= CONVERSATION BINDING =================
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
        refreshMessages();
    }

    public void setOnConversationUpdated(Runnable onConversationUpdated) {
        this.onConversationUpdated = onConversationUpdated;
    }

    // ================= MESSAGE RENDER =================
    private void refreshMessages() {
        messageBox.getChildren().clear();
        for (Message msg : conversation.getMessages()) {
            messageBox.getChildren().add(createBubble(msg));
        }
        scrollToBottom();
    }

    // ================= SEND FLOW =================
    private void sendMessage() {
        if (conversation == null) {
            return;
        }

        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        int previousSize = conversation.getMessages().size();
        String previousTitle = conversation.getTitle();
        inputArea.clear();
        chatService.sendMessage(conversation, text);

        for (int i = previousSize; i < conversation.getMessages().size(); i++) {
            HBox bubble = createBubble(conversation.getMessages().get(i));
            messageBox.getChildren().add(bubble);
            playFadeIn(bubble);
        }
        if (onConversationUpdated != null && !Objects.equals(previousTitle, conversation.getTitle())) {
            onConversationUpdated.run();
        }
        scrollToBottom();
    }

    // ================= BUBBLE FACTORY =================
    private HBox createBubble(Message msg) {
        HBox row = new HBox();
        row.getStyleClass().add("message-row");

        Label text = new Label(msg.getContent());
        text.setWrapText(true);
        text.setMaxWidth(520);
        text.getStyleClass().add("message-text");

        VBox bubble = new VBox(text);
        bubble.getStyleClass().add("message-bubble");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (msg.getSender() == Message.Sender.USER) {
            row.setAlignment(Pos.TOP_RIGHT);
            bubble.getStyleClass().add("user-bubble");
            row.getChildren().addAll(spacer, bubble);
        } else {
            row.setAlignment(Pos.TOP_LEFT);
            bubble.getStyleClass().add("bot-bubble");
            row.getChildren().addAll(bubble, spacer);
        }

        return row;
    }

    // ================= MESSAGE ANIMATION =================
    private void playFadeIn(HBox bubbleRow) {
        bubbleRow.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(220), bubbleRow);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // ================= SCROLL HELPERS =================
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

}
