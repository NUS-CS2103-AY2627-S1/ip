package goop.gui;

import java.io.IOException;

import goop.Goop;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the JavaFX graphical interface for Goop.
 */
public class Main extends Application {
    private final Goop goop = new Goop();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setGoop(goop);

        stage.setTitle("Goop");
        stage.setMinHeight(500.0);
        stage.setMinWidth(420.0);
        stage.setScene(new Scene(mainLayout));
        stage.show();
    }
}
