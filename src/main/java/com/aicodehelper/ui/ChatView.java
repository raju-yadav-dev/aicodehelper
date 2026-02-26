package com.aicodehelper.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Main chat panel containing scrollable message area and input composer.
 */
public class ChatView extends BorderPane {
    private final VBox messagesBox = new VBox(6);
    private final ScrollPane scrollPane = new ScrollPane(messagesBox);
    private final TextArea inputArea = new TextArea();
    private final Button sendIconButton = new Button("↑");
    private final Button themeButton = new Button("Light");
    private final Label typingLabel = new Label("AI is typing...");
    // searchField moved to sidebar
    // private final javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();

    public ChatView() {
        getStyleClass().add("chat-root");
        setPadding(new Insets(16));

        // top header now only contains typing label and theme button placeholder
        themeButton.getStyleClass().add("theme-button");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox headerRow = new HBox(10, spacer, themeButton);
        headerRow.setAlignment(Pos.CENTER);
        typingLabel.getStyleClass().add("typing-label");
        typingLabel.setVisible(false);
        typingLabel.setManaged(false);
        VBox top = new VBox(6, headerRow, typingLabel);
        setTop(top);

        messagesBox.getStyleClass().add("messages-box");
        messagesBox.setPadding(new Insets(8));

        scrollPane.getStyleClass().add("chat-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        StackPane chatContainer = new StackPane(scrollPane);
        chatContainer.getStyleClass().add("chat-container");
        setCenter(chatContainer);

        inputArea.setPromptText("Ask a coding question, paste code, or describe an error...");
        inputArea.setWrapText(true);
        inputArea.getStyleClass().add("input-area");
        inputArea.setPrefRowCount(2);
        inputArea.setMinHeight(72);
        inputArea.textProperty().addListener((obs, oldText, newText) -> resizeInput());

        // Create send icon button
        sendIconButton.getStyleClass().add("send-icon-button");
        sendIconButton.setOnAction(e -> {
            // This will be wired in MainLayout
        });

        // Stack the input area and send button
        StackPane inputContainer = new StackPane(inputArea, sendIconButton);
        StackPane.setAlignment(sendIconButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(sendIconButton, new Insets(0, 12, 0, 0));

        HBox inputRow = new HBox(10, inputContainer);
        inputRow.getStyleClass().add("composer");
        inputRow.setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setHgrow(inputContainer, Priority.ALWAYS);
        inputRow.setPadding(new Insets(10));

        setBottom(inputRow);
    }

    private void resizeInput() {
        int lines = Math.max(1, Math.min(4, inputArea.getText().split("\\R", -1).length));
        inputArea.setPrefRowCount(lines);
    }

    public VBox getMessagesBox() {
        return messagesBox;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public TextArea getInputArea() {
        return inputArea;
    }

    public Button getSendIconButton() {
        return sendIconButton;
    }

    public Button getThemeButton() {
        return themeButton;
    }

    public Label getTypingLabel() {
        return typingLabel;
    }

    public void resetInput() {
        inputArea.clear();
        inputArea.setPrefRowCount(2);
    }
}
