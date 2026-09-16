package nova;

import gui.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX application for NOVA.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Nova nova =
                new Nova("./data/nova.txt");

        MainWindow mainWindow =
                new MainWindow(nova);

        Scene scene =
                new Scene(mainWindow, 600, 500);

        stage.setTitle("NOVA");
        stage.setScene(scene);
        stage.setMinWidth(450);
        stage.setMinHeight(500);
        stage.setResizable(true);
        stage.show();
    }
}
