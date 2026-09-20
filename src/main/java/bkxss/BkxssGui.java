package bkxss;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** Starts the JavaFX user interface for the Bkxss task chatbot. */
public class BkxssGui extends Application {
    private static final String MAIN_VIEW_PATH = "/view/MainWindow.fxml";
    private static final String STYLESHEET_PATH = "/styles/bkxss.css";

    /**
     * Loads the FXML view, supplies its controller with application data, and displays the window.
     *
     * @param stage primary JavaFX window
     * @throws IOException if the bundled FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        Storage storage = new Storage("data/bkxss.txt");
        ArrayList<Task> tasks = storage.load();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW_PATH));
        BorderPane root = loader.load();
        MainWindow controller = loader.getController();
        controller.setDependencies(tasks, storage);

        Scene scene = new Scene(root, 620, 560);
        scene.getStylesheets().add(getClass().getResource(STYLESHEET_PATH).toExternalForm());
        configureStage(stage, scene);
        controller.requestInputFocus();
    }

    /** Applies window constraints and displays the scene. */
    private static void configureStage(Stage stage, Scene scene) {
        stage.setTitle("Bkxss · Task Assistant");
        stage.setMinWidth(360);
        stage.setMinHeight(360);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}
