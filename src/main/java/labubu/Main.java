package labubu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Starts the Labubu JavaFX application. */
public class Main extends Application {
    private static final double MIN_WINDOW_HEIGHT = 500;

    /** Displays the reusable main window component. */
    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow(new Labubu("data/labubu.txt"));
        Scene scene = new Scene(mainWindow, 700, 650);
        addStylesheets(scene,
                "/css/main-window.css",
                "/css/input-field.css",
                "/css/speech-bubble.css",
                "/css/help-panel.css");
        stage.setTitle("Labubu - Task Tracker");
        stage.setScene(scene);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setMinWidth(417);
        stage.show();
    }

    private void addStylesheets(Scene scene, String... stylesheetPaths) {
        for (String stylesheetPath : stylesheetPaths) {
            scene.getStylesheets().add(getClass().getResource(stylesheetPath).toExternalForm());
        }
    }
}
