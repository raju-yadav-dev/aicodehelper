package com.example.chatbot.controller;

import com.example.chatbot.model.Conversation;
import com.example.chatbot.service.ChatService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    // ================= SIDEBAR + CONTENT NODES =================
    @FXML
    private VBox sidebar;
    @FXML
    private Button newChatButton;
    @FXML
    private ListView<Conversation> chatList;
    @FXML
    private VBox chatContainer;
    @FXML
    private HBox titleBar;
    @FXML
    private BorderPane appShell;
    @FXML
    private StackPane windowRoot;

    // ================= TITLE BAR ACTION NODES =================
    @FXML
    private MenuButton settingsButton;
    @FXML
    private RadioMenuItem themeDarkItem;
    @FXML
    private RadioMenuItem themeLightItem;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button closeButton;

    // ================= STATE =================
    private final ChatService chatService = new ChatService();
    private Stage stage;
    private double dragOffsetX;
    private double dragOffsetY;
    private Rectangle shellClip;

    // ================= INITIALIZATION =================
    @FXML
    public void initialize() {
        // ---- Sidebar Actions + Selection ----
        newChatButton.setOnAction(e -> createNewConversation());
        chatList.setCellFactory(list -> new ConversationCell());
        chatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadConversation(newVal);
            }
        });

        // ---- Window Chrome ----
        setupTitleBarDrag();
        applyRoundedClip();
        wireWindowButtons();
        wireThemeMenu();

        // ---- Default Theme + First Conversation ----
        applyTheme("theme-dark");

        Conversation first = chatService.createConversation();
        chatList.getItems().add(first);
        chatList.getSelectionModel().select(first);
    }

    // ================= STAGE BINDING =================
    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null) {
            stage.maximizedProperty().addListener((obs, oldVal, isMaximized) -> updateMaximizedClass(isMaximized));
            updateMaximizedClass(stage.isMaximized());
        }
    }

    // ================= TITLE BAR DRAG =================
    private void setupTitleBarDrag() {
        titleBar.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || isWindowControl(event.getTarget())) {
                return;
            }
            dragOffsetX = event.getSceneX();
            dragOffsetY = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            if (stage == null || event.getButton() != MouseButton.PRIMARY || isWindowControl(event.getTarget())) {
                return;
            }

            if (stage.isMaximized()) {
                double dragRatio = event.getSceneX() / Math.max(1.0, titleBar.getWidth());
                stage.setMaximized(false);
                updateMaximizedClass(false);
                stage.setX(event.getScreenX() - stage.getWidth() * dragRatio);
                stage.setY(event.getScreenY() - dragOffsetY);
                return;
            }

            stage.setX(event.getScreenX() - dragOffsetX);
            stage.setY(event.getScreenY() - dragOffsetY);
        });

        titleBar.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !isWindowControl(event.getTarget())) {
                toggleMaximize();
            }
        });
    }

    // ================= TITLE BAR HIT TEST =================
    private boolean isWindowControl(Object target) {
        if (!(target instanceof Node node)) {
            return false;
        }
        Node current = node;
        while (current != null) {
            if (current instanceof ButtonBase) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    // ================= WINDOW BUTTON ACTIONS =================
    private void wireWindowButtons() {
        bindWindowAction(closeButton, () -> stage.close());
        bindWindowAction(minimizeButton, () -> stage.setIconified(true));
        bindWindowAction(maximizeButton, this::toggleMaximize);
    }

    // ================= BUTTON HELPER =================
    private void bindWindowAction(Button button, Runnable action) {
        button.setOnAction(e -> {
            if (stage != null) {
                action.run();
            }
        });
    }

    // ================= MAXIMIZE TOGGLE =================
    private void toggleMaximize() {
        if (stage == null) {
            return;
        }
        stage.setMaximized(!stage.isMaximized());
        updateMaximizedClass(stage.isMaximized());
    }

    // ================= MAXIMIZED CSS STATE =================
    private void updateMaximizedClass(boolean maximized) {
        if (appShell == null || windowRoot == null) {
            return;
        }
        if (maximized) {
            if (!appShell.getStyleClass().contains("maximized")) {
                appShell.getStyleClass().add("maximized");
            }
            if (!windowRoot.getStyleClass().contains("maximized")) {
                windowRoot.getStyleClass().add("maximized");
            }
        } else {
            appShell.getStyleClass().remove("maximized");
            windowRoot.getStyleClass().remove("maximized");
        }
        if (shellClip != null) {
            shellClip.setArcWidth(maximized ? 0 : 32);
            shellClip.setArcHeight(maximized ? 0 : 32);
        }
    }

    // ================= ROUNDED SHELL CLIP =================
    private void applyRoundedClip() {
        shellClip = new Rectangle();
        shellClip.setArcWidth(32);
        shellClip.setArcHeight(32);
        appShell.setClip(shellClip);

        appShell.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            shellClip.setWidth(newBounds.getWidth());
            shellClip.setHeight(newBounds.getHeight());
        });

        Platform.runLater(() -> {
            shellClip.setWidth(appShell.getWidth());
            shellClip.setHeight(appShell.getHeight());
        });
    }

    // ================= CONVERSATION ACTIONS =================
    private void createNewConversation() {
        Conversation conv = chatService.createConversation();
        chatList.getItems().add(conv);
        chatList.getSelectionModel().select(conv);
    }

    // ================= CHAT VIEW LOADING =================
    private void loadConversation(Conversation conversation) {
        try {
            // ---- Load Chat FXML + Inject Conversation ----
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat.fxml"));
            Node chatPane = loader.load();
            ChatController controller = loader.getController();
            controller.setConversation(conversation);

            // ---- Replace Center Content + Stretch to Fill ----
            chatContainer.getChildren().setAll(chatPane);
            VBox.setVgrow(chatPane, Priority.ALWAYS);
            if (chatPane instanceof Region region) {
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ================= THEME MENU =================
    private void wireThemeMenu() {
        // ---- Mutually Exclusive Theme Items ----
        ToggleGroup group = new ToggleGroup();
        themeDarkItem.setToggleGroup(group);
        themeLightItem.setToggleGroup(group);

        // ---- Theme Switch Actions ----
        themeDarkItem.setOnAction(e -> applyTheme("theme-dark"));
        themeLightItem.setOnAction(e -> applyTheme("theme-light"));
    }

    // ================= THEME APPLY =================
    private void applyTheme(String themeClass) {
        windowRoot.getStyleClass().removeAll("theme-dark", "theme-light");
        windowRoot.getStyleClass().add(themeClass);
        themeDarkItem.setSelected("theme-dark".equals(themeClass));
        themeLightItem.setSelected("theme-light".equals(themeClass));
    }
}
