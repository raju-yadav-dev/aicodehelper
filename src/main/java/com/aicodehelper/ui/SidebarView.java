package com.aicodehelper.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Left sidebar with title, new chat action, and chat history list.
 */
public class SidebarView extends VBox {
    private final Button newChatButton = new Button("+ New Chat");
    private final ListView<String> historyView = new ListView<>();

    public SidebarView() {
        getStyleClass().add("sidebar");
        setPadding(new Insets(16));
        setSpacing(12);
        setPrefWidth(250);

        Label logo = new Label("AI Code Helper");
        logo.getStyleClass().add("logo");

        Label subtitle = new Label("for Beginners");
        subtitle.getStyleClass().add("logo-subtitle");

        newChatButton.getStyleClass().add("new-chat-button");

        historyView.getStyleClass().add("history-list");
        VBox.setVgrow(historyView, Priority.ALWAYS);

        getChildren().addAll(logo, subtitle, newChatButton, historyView);
        setAlignment(Pos.TOP_LEFT);
    }

    public Button getNewChatButton() {
        return newChatButton;
    }

    public ListView<String> getHistoryView() {
        return historyView;
    }
}