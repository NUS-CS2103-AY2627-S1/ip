package happybot;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Displays HappyBot in a JavaFX window.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 650;

    private HappyBot happyBot;

    @Override
    public void start(Stage stage) {
        URL viewUrl = Main.class.getResource("/view/MainWindow.fxml");
        if (viewUrl == null) {
            showStartupError();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(viewUrl);
            Scene scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
            MainWindow mainWindow = fxmlLoader.getController();
            happyBot = new HappyBot();
            mainWindow.setHappyBot(happyBot);

            stage.setTitle("HappyBot");
            stage.setMinWidth(WINDOW_WIDTH);
            stage.setMinHeight(WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | RuntimeException e) {
            if (happyBot != null) {
                happyBot.close();
                happyBot = null;
            }

            showStartupError();
        }
    }

    @Override
    public void stop() {
        if (happyBot != null) {
            happyBot.close();
        }
    }

    /**
     * Shows a concise error when a required graphical resource cannot be loaded.
     */
    private void showStartupError() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("HappyBot could not start.");
        errorAlert.setContentText("A required interface file is unavailable. Please reinstall HappyBot.");
        errorAlert.showAndWait();
        Platform.exit();
    }
}
