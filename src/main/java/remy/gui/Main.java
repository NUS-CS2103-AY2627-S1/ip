package remy.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import remy.Remy;

/**
 * Starts Remy's JavaFX graphical user interface.
 */
public class Main extends Application {

    /** Chatbot used by the main window. */
    private final Remy remy = new Remy();

    /** Creates and displays Remy's main application window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(Main.class
                            .getResource("/view/MainWindow.css")
                            .toExternalForm());

            stage.setTitle("Remy");
            stage.setMinWidth(380);
            stage.setMinHeight(520);
            stage.getIcons()
                    .add(new Image(
                            Main.class
                                    .getResourceAsStream("/images/Remy.png")));
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setRemy(remy);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Remy's main window.", e);
        }
    }
}
