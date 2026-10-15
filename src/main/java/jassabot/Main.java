package jassabot;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for JassaBot using FXML.
 */
public class Main extends Application {

    private final JassaBot jassaBot = new JassaBot(Path.of("data", "jassabot.txt"));

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("JassaBot");
            stage.setMinHeight(420);
            stage.setMinWidth(400);
            fxmlLoader.<MainWindow>getController().setJassaBot(jassaBot);
            stage.show();

        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the JassaBot window.", e);
        }
    }
}
