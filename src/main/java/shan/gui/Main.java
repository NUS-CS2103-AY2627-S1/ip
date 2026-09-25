package shan.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shan.Shan;

/**
 * Provides the JavaFX graphical interface for Shan using FXML.
 */
public class Main extends Application {
    private final Shan shan = new Shan();

    /**
     * Constructs the JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setShan(this.shan);

        stage.setTitle("Shan");
        stage.setMinHeight(220.0);
        stage.setMinWidth(417.0);
        stage.setScene(new Scene(mainLayout));
        stage.show();
    }
}
