package duke;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX entry point that creates the main window and starts the GUI.
 */
public class Main extends Application {

    private final Duke duke = new Duke();

    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow();
        Scene scene = new Scene(mainWindow);
        scene.getStylesheets().add(Main.class.getResource("/css/dialog-box.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Ducke");
        mainWindow.setDuke(duke);
        stage.show();
    }
}
