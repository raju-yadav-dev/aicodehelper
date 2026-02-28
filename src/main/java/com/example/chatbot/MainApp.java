package com.example.chatbot;

import com.example.chatbot.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    // ================= WINDOW RESIZE CONSTANTS =================
    private static final double RESIZE_BORDER = 8;

    // ================= APPLICATION STARTUP =================
    @Override
    public void start(Stage primaryStage) throws Exception {
        // ---- Load Main Layout + Controller ----
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();

        // ---- Configure Scene + Styles ----
        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        // ---- Configure Stage ----
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Chatbot Desktop");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(620);
        primaryStage.setResizable(true);

        // ---- Connect Controller + Enable Border Resize ----
        controller.setStage(primaryStage);
        enableResize(primaryStage, scene);

        // ---- Show Window ----
        primaryStage.show();
    }

    // ================= CUSTOM WINDOW RESIZE =================
    private void enableResize(Stage stage, Scene scene) {
        // ---- Resize Edge Tracking State ----
        final boolean[] resizeTop = {false};
        final boolean[] resizeBottom = {false};
        final boolean[] resizeLeft = {false};
        final boolean[] resizeRight = {false};
        final double[] pressScreenX = {0};
        final double[] pressScreenY = {0};
        final double[] pressStageX = {0};
        final double[] pressStageY = {0};
        final double[] pressWidth = {0};
        final double[] pressHeight = {0};

        // ---- Update Cursor When Hovering Resize Borders ----
        scene.setOnMouseMoved(event -> {
            if (stage.isMaximized()) {
                scene.setCursor(Cursor.DEFAULT);
                return;
            }
            boolean left = event.getSceneX() <= RESIZE_BORDER;
            boolean right = event.getSceneX() >= scene.getWidth() - RESIZE_BORDER;
            boolean top = event.getSceneY() <= RESIZE_BORDER;
            boolean bottom = event.getSceneY() >= scene.getHeight() - RESIZE_BORDER;
            scene.setCursor(getCursor(top, right, bottom, left));
        });

        // ---- Capture Initial Pointer + Stage Bounds ----
        scene.setOnMousePressed(event -> {
            if (stage.isMaximized()) {
                return;
            }
            Cursor cursor = scene.getCursor();
            resizeTop[0] = cursor == Cursor.N_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.NW_RESIZE;
            resizeBottom[0] = cursor == Cursor.S_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.SW_RESIZE;
            resizeLeft[0] = cursor == Cursor.W_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.SW_RESIZE;
            resizeRight[0] = cursor == Cursor.E_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.SE_RESIZE;
            pressScreenX[0] = event.getScreenX();
            pressScreenY[0] = event.getScreenY();
            pressStageX[0] = stage.getX();
            pressStageY[0] = stage.getY();
            pressWidth[0] = stage.getWidth();
            pressHeight[0] = stage.getHeight();
        });

        // ---- Apply Resize While Dragging ----
        scene.setOnMouseDragged(event -> {
            if (stage.isMaximized()) {
                return;
            }
            double dx = event.getScreenX() - pressScreenX[0];
            double dy = event.getScreenY() - pressScreenY[0];

            if (resizeRight[0]) {
                stage.setWidth(Math.max(stage.getMinWidth(), pressWidth[0] + dx));
            }
            if (resizeBottom[0]) {
                stage.setHeight(Math.max(stage.getMinHeight(), pressHeight[0] + dy));
            }
            if (resizeLeft[0]) {
                double newWidth = Math.max(stage.getMinWidth(), pressWidth[0] - dx);
                double clampedDx = pressWidth[0] - newWidth;
                stage.setX(pressStageX[0] + clampedDx);
                stage.setWidth(newWidth);
            }
            if (resizeTop[0]) {
                double newHeight = Math.max(stage.getMinHeight(), pressHeight[0] - dy);
                double clampedDy = pressHeight[0] - newHeight;
                stage.setY(pressStageY[0] + clampedDy);
                stage.setHeight(newHeight);
            }
        });
    }

    // ================= CURSOR MAPPING =================
    private Cursor getCursor(boolean top, boolean right, boolean bottom, boolean left) {
        if (top && left) {
            return Cursor.NW_RESIZE;
        }
        if (top && right) {
            return Cursor.NE_RESIZE;
        }
        if (bottom && left) {
            return Cursor.SW_RESIZE;
        }
        if (bottom && right) {
            return Cursor.SE_RESIZE;
        }
        if (top) {
            return Cursor.N_RESIZE;
        }
        if (bottom) {
            return Cursor.S_RESIZE;
        }
        if (left) {
            return Cursor.W_RESIZE;
        }
        if (right) {
            return Cursor.E_RESIZE;
        }
        return Cursor.DEFAULT;
    }

    // ================= ENTRY POINT =================
    public static void main(String[] args) {
        launch(args);
    }
}
