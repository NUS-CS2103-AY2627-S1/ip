package oreo.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import oreo.Oreo;

/** Displays Oreo in a JavaFX window. */
public class Main extends Application {
    /** Starts the main Oreo window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setOreo(new Oreo());
            stage.setScene(new Scene(root));
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.setTitle(Ui.PRODUCT_NAME);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the Oreo GUI.", e);
        }
    }
}
