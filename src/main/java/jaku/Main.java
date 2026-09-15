package jaku;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX entry point for Jaku. */
public class Main extends Application {
    /** Creates the JavaFX application entry point. */
    public Main() {
    }

    /** Creates and displays Jaku's chat window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            loader.<jaku.ui.MainWindow>getController().setJaku(new Jaku());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Jaku — Calm task companion");
            stage.setMinHeight(520.0);
            stage.setMinWidth(540.0);
            stage.setHeight(680.0);
            stage.setWidth(760.0);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Jaku's main window.", exception);
        }
    }
}
