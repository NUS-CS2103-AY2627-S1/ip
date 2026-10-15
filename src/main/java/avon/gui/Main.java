package avon.gui;

import java.io.IOException;

import avon.Avon;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Avon's JavaFX user interface.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 600;
    private static final double MINIMUM_WINDOW_WIDTH = 420;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setAvon(new Avon());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/view/avon.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Avon");
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.show();
    }
}
