package larp;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Loads and displays Larp's JavaFX graphical interface.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "data/larp.txt";

    /**
     * Creates the JavaFX application instance.
     */
    public Main() {
    }

    /**
     * Creates the main window and connects it to the Larp application logic.
     *
     * @param stage Primary window supplied by JavaFX.
     * @throws IOException If the FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowUrl = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "MainWindow.fxml is missing");
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowUrl);
        BorderPane root = fxmlLoader.load();

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setLarp(new Larp(DEFAULT_FILE_PATH));

        Scene scene = new Scene(root, 680, 720);
        URL stylesheetUrl = Objects.requireNonNull(
                Main.class.getResource("/css/main.css"),
                "main.css is missing");
        scene.getStylesheets().add(stylesheetUrl.toExternalForm());

        stage.setTitle("Larp");
        stage.setMinWidth(480);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }
}
