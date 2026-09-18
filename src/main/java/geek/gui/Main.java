package geek.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import geek.Geek;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Configures and displays the Geek JavaFX window.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String MAIN_STYLESHEET = "/css/main.css";
    private static final String DIALOG_STYLESHEET =
            "/css/dialog-box.css";

    private final Geek geek = new Geek();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                requireResource(MAIN_WINDOW_FXML)
        );
        Parent root = loader.load();
        loader.<MainWindow>getController().setGeek(geek);

        Scene scene = new Scene(root, 620, 760);
        scene.getStylesheets().add(
                requireResource(MAIN_STYLESHEET)
                        .toExternalForm()
        );
        scene.getStylesheets().add(
                requireResource(DIALOG_STYLESHEET)
                        .toExternalForm()
        );

        stage.setTitle("Geek — Calm Mission Control");
        stage.setMinWidth(500);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    private static URL requireResource(String path) {
        return Objects.requireNonNull(
                Main.class.getResource(path),
                "Missing application resource: " + path
        );
    }
}
