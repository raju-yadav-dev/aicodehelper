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
import javafx.scene.layout.VBox;

/**
 * Main chat panel containing scrollable message area and input composer.
 */
public class ChatView extends BorderPane {
    private final VBox messagesBox = new VBox(6);
    private final ScrollPane scrollPane = new ScrollPane(messagesBox);
    private final TextArea inputArea = new TextArea();
    private final Button sendButton = new Button("Send");
    private final Button themeButton = new Button("Light");
    private final Label typingLabel = new Label("AI is typing...");

    public ChatView() {
        getStyleClass().add("chat-root");
        setPadding(new Insets(16));

        Label title = new Label("AI Code Helper for Beginners");
        title.getStyleClass().add("chat-title");
        Label subtitle = new Label("Learn coding with guided, structured AI answers");
        subtitle.getStyleClass().add("chat-subtitle");

        typingLabel.getStyleClass().add("typing-label");
        typingLabel.setVisible(false);
        typingLabel.setManaged(false);

        themeButton.getStyleClass().add("theme-button");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        VBox titleBlock = new VBox(3, title, subtitle);
        HBox titleRow = new HBox(10, titleBlock, topSpacer, themeButton);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(6, titleRow, typingLabel);
        setTop(top);

        messagesBox.getStyleClass().add("messages-box");
        messagesBox.setPadding(new Insets(8));

        scrollPane.getStyleClass().add("chat-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        setCenter(scrollPane);

        inputArea.setPromptText("Ask a coding question, paste code, or describe an error...");
        inputArea.setWrapText(true);
        inputArea.getStyleClass().add("input-area");
        inputArea.setPrefRowCount(2);
        inputArea.setMinHeight(72);
        inputArea.textProperty().addListener((obs, oldText, newText) -> resizeInput());

        sendButton.getStyleClass().add("send-button");

        HBox inputRow = new HBox(10, inputArea, sendButton);
        inputRow.getStyleClass().add("composer");
        inputRow.setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setHgrow(inputArea, Priority.ALWAYS);
        inputRow.setPadding(new Insets(10));

        setBottom(inputRow);
    }

    private void resizeInput() {
        int lines = Math.max(2, Math.min(8, inputArea.getText().split("\\R", -1).length));
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

    public Button getSendButton() {
        return sendButton;
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
