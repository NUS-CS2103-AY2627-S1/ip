package es.gui;

import java.io.IOException;

import es.Es;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
/** JavaFX entry point for Es. */
public class Main extends Application {
    private final Es es = new Es();

    @Override public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setEs(es);
            loader.<MainWindow>getController().showWelcome();
            stage.setScene(new Scene(root));
            stage.setTitle("Es");
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Es interface", e);
        }
    }
}
