package com.aicodehelper.ui;

import com.aicodehelper.model.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * Reusable chat bubble component supporting normal and code style content.
 */
public class ChatBubble extends HBox {
    public ChatBubble(Message message, Consumer<String> onCopy) {
        getStyleClass().add("message-row");

        VBox bubble = message.getSender() == Message.Sender.USER
                ? buildUserBubble(message)
                : buildAssistantCard(message, onCopy);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (message.getSender() == Message.Sender.USER) {
            setAlignment(Pos.CENTER_RIGHT);
            getChildren().addAll(spacer, bubble);
        } else {
            setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(bubble, spacer);
        }

        setPadding(new Insets(4, 8, 4, 8));
    }

    private VBox buildUserBubble(Message message) {
        VBox bubble = new VBox(8);
        bubble.getStyleClass().addAll("bubble", "user-bubble");

        Label header = new Label("You");
        header.getStyleClass().add("bubble-header");

        Label content = new Label(message.getContent());
        content.setWrapText(true);
        content.getStyleClass().add("bubble-text");

        bubble.getChildren().addAll(header, content);
        return bubble;
    }

    private VBox buildAssistantCard(Message message, Consumer<String> onCopy) {
        VBox bubble = new VBox(10);
        bubble.getStyleClass().addAll("bubble", "bot-bubble", "answer-card");

        Label header = new Label("AI Helper");
        header.getStyleClass().add("bubble-header");
        bubble.getChildren().add(header);

        renderMarkdownSections(message.getContent(), bubble, onCopy);
        return bubble;
    }

    private void renderMarkdownSections(String text, VBox container, Consumer<String> onCopy) {
        List<String> lines = List.of(text.split("\\R", -1));
        boolean inCode = false;
        String codeLanguage = "";
        StringBuilder codeBuilder = new StringBuilder();

        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine;

            if (line.startsWith("```")) {
                if (!inCode) {
                    inCode = true;
                    codeLanguage = line.replace("```", "").trim();
                    codeBuilder.setLength(0);
                } else {
                    addCodeBlock(container, codeBuilder.toString().trim(), codeLanguage, onCopy);
                    inCode = false;
                    codeLanguage = "";
                    codeBuilder.setLength(0);
                }
                continue;
            }

            if (inCode) {
                codeBuilder.append(line).append(System.lineSeparator());
                continue;
            }

            if (line.startsWith("## ")) {
                Label heading = new Label(line.substring(3).trim());
                heading.getStyleClass().add("answer-h2");
                heading.setWrapText(true);
                container.getChildren().add(heading);
                continue;
            }

            if (line.startsWith("### ")) {
                Label subHeading = new Label(line.substring(4).trim());
                subHeading.getStyleClass().add("answer-h3");
                subHeading.setWrapText(true);
                container.getChildren().add(subHeading);
                continue;
            }

            if (line.startsWith("- ")) {
                Label bullet = new Label("\u2022 " + line.substring(2).trim());
                bullet.getStyleClass().add("answer-bullet");
                bullet.setWrapText(true);
                container.getChildren().add(bullet);
                continue;
            }

            if (line.isBlank()) {
                Region gap = new Region();
                gap.setMinHeight(6);
                container.getChildren().add(gap);
                continue;
            }

            Label paragraph = new Label(line);
            paragraph.getStyleClass().add("answer-text");
            paragraph.setWrapText(true);
            container.getChildren().add(paragraph);
        }

        if (inCode && !codeBuilder.isEmpty()) {
            addCodeBlock(container, codeBuilder.toString().trim(), codeLanguage, onCopy);
        }
    }

    private void addCodeBlock(VBox container, String code, String language, Consumer<String> onCopy) {
        if (code.isBlank()) {
            return;
        }

        VBox codeCard = new VBox(8);
        codeCard.getStyleClass().add("code-panel");

        Label languageBadge = new Label(language == null || language.isBlank() ? "Code" : language);
        languageBadge.getStyleClass().add("code-badge");

        Button copy = new Button("Copy");
        copy.getStyleClass().add("copy-button");
        copy.setOnAction(event -> onCopy.accept(code));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox head = new HBox(8, languageBadge, spacer, copy);
        head.setAlignment(Pos.CENTER_LEFT);

        Label codeText = new Label(code);
        codeText.setWrapText(true);
        codeText.getStyleClass().add("code-text");

        codeCard.getChildren().addAll(head, codeText);
        container.getChildren().add(codeCard);
    }
}
