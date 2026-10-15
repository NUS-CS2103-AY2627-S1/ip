package spoon;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import spoon.ui.MainWindow;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private final Spoon spoon = new Spoon();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);

            // Title
            stage.setTitle("Spoon");
            fxmlLoader.<MainWindow>getController().setSpoon(spoon);

            // Formatting
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
