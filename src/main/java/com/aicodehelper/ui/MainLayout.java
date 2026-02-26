package com.aicodehelper.ui;

import com.aicodehelper.controller.ChatController;
import com.aicodehelper.model.Message;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Composes UI components and binds them to controller behavior.
 */
public class MainLayout {
    private final ChatController chatController = new ChatController();
    private final SidebarView sidebarView = new SidebarView();
    private final ChatView chatView = new ChatView();
    private BorderPane root;

    public void init(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("app-root");

        Region divider = new Region();
        divider.getStyleClass().add("divider");
        divider.setPrefWidth(1);

        HBox centerContainer = new HBox(sidebarView, divider, chatView);
        HBox.setHgrow(chatView, Priority.ALWAYS);
        centerContainer.setMinSize(0, 0);

        root.setCenter(centerContainer);
        root.setMinSize(0, 0);

        javafx.geometry.Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(1200, Math.max(900, visualBounds.getWidth() * 0.9));
        double sceneHeight = Math.min(760, Math.max(620, visualBounds.getHeight() * 0.9));
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.getStylesheets().add(MainLayout.class.getResource("/styles/app.css").toExternalForm());

        wireActions();
        chatController.startNewChat();
        refreshHistory();

        stage.setTitle("Cortex");
        stage.setScene(scene);
        stage.setMinWidth(780);
        stage.setMinHeight(540);
        stage.setMaxWidth(visualBounds.getWidth());
        stage.setMaxHeight(visualBounds.getHeight());
        stage.show();
    }

    private void wireActions() {
        sidebarView.getNewChatButton().setOnAction(e -> {
            chatController.startNewChat();
            chatView.getMessagesBox().getChildren().clear();
            refreshHistory();
        });

        chatView.getSendIconButton().setOnAction(e -> sendMessage());
        chatView.getThemeButton().setOnAction(e -> toggleTheme());
        chatView.getInputArea().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                sendMessage();
                event.consume();
            }
        });
    }

    private void sendMessage() {
        String input = chatView.getInputArea().getText();
        if (input == null || input.isBlank()) {
            return;
        }

        Message userMessage = chatController.createUserMessage(input.trim());
        chatController.getMessages().add(userMessage);
        addMessageBubble(userMessage);

        chatView.resetInput();
        runTypingAnimation(() -> {
            Message botMessage = chatController.createBotReply(input);
            chatController.getMessages().add(botMessage);
            addMessageBubble(botMessage);
        });
    }

    private void addMessageBubble(Message message) {
        ChatBubble bubble = new ChatBubble(message, copiedText -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(copiedText);
            clipboard.setContent(content);
            showTemporaryStatus("Copied to clipboard");
        });

        // start invisible and slightly shifted
        bubble.setOpacity(0);
        bubble.setTranslateY(10);

        chatView.getMessagesBox().getChildren().add(bubble);

        // animation
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), bubble);
        fade.setToValue(1);
        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(300), bubble);
        slide.setToY(0);
        javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, slide);
        pt.setOnFinished(e -> autoScrollToBottom());
        pt.play();
    }

    private void autoScrollToBottom() {
        Platform.runLater(() -> {
            javafx.beans.property.DoubleProperty v = chatView.getScrollPane().vvalueProperty();
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(300),
                            new javafx.animation.KeyValue(v, 1.0, javafx.animation.Interpolator.EASE_BOTH))
            );
            timeline.play();
        });
    }

    private void runTypingAnimation(Runnable onFinished) {
        chatView.getTypingLabel().setManaged(true);
        chatView.getTypingLabel().setVisible(true);
        chatView.getTypingLabel().setText("AI is typing...");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(900), event -> {
                    chatView.getTypingLabel().setVisible(false);
                    chatView.getTypingLabel().setManaged(false);
                    onFinished.run();
                })
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void refreshHistory() {
        sidebarView.getHistoryView().setItems(FXCollections.observableArrayList(chatController.getChatHistory()));
    }

    private void toggleTheme() {
        boolean lightMode = root.getStyleClass().contains("light-mode");
        if (lightMode) {
            root.getStyleClass().remove("light-mode");
            chatView.getThemeButton().setText("Light");
            return;
        }
        root.getStyleClass().add("light-mode");
        chatView.getThemeButton().setText("Dark");
    }

    private void showTemporaryStatus(String status) {
        chatView.getTypingLabel().setText(status);
        chatView.getTypingLabel().setManaged(true);
        chatView.getTypingLabel().setVisible(true);

        Timeline clear = new Timeline(new KeyFrame(Duration.millis(1200), event -> {
            chatView.getTypingLabel().setVisible(false);
            chatView.getTypingLabel().setManaged(false);
        }));
        clear.setCycleCount(1);
        clear.play();
    }
}
