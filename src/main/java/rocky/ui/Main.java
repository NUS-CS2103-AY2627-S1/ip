package rocky.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for Rocky: a chat window that accepts the same text commands
 * as the original text UI.
 */
public class Main extends Application {
    private static final double MIN_WIDTH = 360;
    private static final double MIN_HEIGHT = 400;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new MainWindow());

        stage.setTitle("Rocky");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
