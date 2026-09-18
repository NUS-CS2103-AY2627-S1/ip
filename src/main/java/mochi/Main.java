package mochi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mochi.ui.MainWindow;

/**
 * JavaFX entry point for Mochi's graphical interface.
 */
public class Main extends Application {
    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Mochi");
        stage.setMinWidth(420);
        stage.setMinHeight(480);
        stage.setScene(new Scene(new MainWindow(), 540, 700));
        stage.show();
    }
}
