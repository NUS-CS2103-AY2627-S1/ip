package org.cs2103t.marquee.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.cs2103t.marquee.gui.ui.MainMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for Marquee GUI.
 */
public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Path getLocalStoragePath() {
        String applicationFolder = "Marquee";
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return Paths.get(System.getenv("LOCALAPPDATA"), applicationFolder);
        } else if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", applicationFolder);
        } else {
            String dataHome = System.getenv("XDG_DATA_HOME");
            String userHome = System.getProperty("user.home");
            return Paths.get(dataHome != null && !dataHome.isEmpty() ? dataHome : userHome, applicationFolder);
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        MainMenu mainMenu = new MainMenu(stage);
        Scene scene = new Scene(mainMenu);
        mainMenu.bindHotkeys(scene);
        stage.setTitle("Marquee: Untitled");
        stage.setScene(scene);
        stage.setMinWidth(mainMenu.getMinWidth());
        stage.setMinHeight(mainMenu.getMinHeight());
        stage.show();
    }
}
