package com.example.chatbot.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.InputStream;
import java.util.Properties;

/**
 * Builds the About section content so MainController stays focused on window flow.
 */
public final class AboutSectionView {
    private AboutSectionView() {
        // Utility class.
    }

    public static VBox createAboutContent(Runnable onCheckForUpdates) {
        Label appName = new Label("Cortex");
        appName.getStyleClass().add("about-app-name");

        String version = "Unknown";
        try (InputStream stream = new java.io.FileInputStream("d:/GitHub/AI-project/cortex/version.properties")) {
            Properties props = new Properties();
            props.load(stream);
            version = props.getProperty("version", version);
        } catch (Exception e) {
            // Optionally log or handle error
        }

        Label versionLabel = new Label("Version: " + version);
        versionLabel.getStyleClass().add("about-version");

        Button updateButton = new Button("Check for Updates");
        updateButton.getStyleClass().add("about-update-button");
        updateButton.setOnAction(event -> {
            updateButton.setText("\u2713 You are running the latest version.");
            updateButton.setDisable(true);
            if (onCheckForUpdates != null) {
                onCheckForUpdates.run();
            }
        });

        VBox content = new VBox(14, appName, versionLabel, updateButton);
        content.getStyleClass().add("about-dialog-root");
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28, 36, 28, 36));
        return content;
    }
}
