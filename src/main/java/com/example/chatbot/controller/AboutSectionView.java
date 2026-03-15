package com.example.chatbot.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Builds the About section content so MainController stays focused on window flow.
 */
public final class AboutSectionView {
    private static final Path VERSION_PROPERTIES_PATH = Paths.get("version.properties");

    private AboutSectionView() {
        // Utility class.
    }

    public static VBox createAboutContent(Runnable onCheckForUpdates) {
        Label appName = new Label("Cortex");
        appName.getStyleClass().add("about-app-name");

        String version = loadVersion();

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

    private static String loadVersion() {
        String version = "Unknown";
        try (InputStream stream = openVersionProperties()) {
            if (stream == null) {
                return version;
            }
            Properties props = new Properties();
            props.load(stream);
            version = props.getProperty("version", version);
        } catch (IOException ignored) {
            // Keep About dialog usable even if version metadata is unavailable.
        }
        return version;
    }

    private static InputStream openVersionProperties() throws IOException {
        if (Files.exists(VERSION_PROPERTIES_PATH)) {
            return Files.newInputStream(VERSION_PROPERTIES_PATH);
        }
        return AboutSectionView.class.getResourceAsStream("/version.properties");
    }
}
