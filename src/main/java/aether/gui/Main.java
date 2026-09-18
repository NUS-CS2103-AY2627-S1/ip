package aether.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import aether.Aether;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** Starts Aether's JavaFX user interface from its FXML layout. */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 560;
    private static final int WINDOW_HEIGHT = 720;

    @Override
    public void start(Stage stage) throws IOException {
        URL layout = Objects.requireNonNull(Main.class.getResource("/view/MainWindow.fxml"));
        FXMLLoader loader = new FXMLLoader(layout);
        BorderPane root = loader.load();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        URL stylesheet = Objects.requireNonNull(Main.class.getResource("/css/main.css"));
        scene.getStylesheets().add(stylesheet.toExternalForm());

        MainWindow controller = loader.getController();
        controller.setAether(new Aether());

        stage.setTitle("Aether");
        stage.setMinWidth(440);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }
}
