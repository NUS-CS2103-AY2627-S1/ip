package ollie;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Displays Ollie's JavaFX graphical interface.
 */
public class Main extends Application {
    private static final double WINDOW_HEIGHT = 640.0;
    private static final double WINDOW_WIDTH = 540.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 420.0;
    private static final double MINIMUM_WINDOW_WIDTH = 360.0;

    private final Ollie ollie = Ollie.createDefault();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        BorderPane root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setOllie(ollie);

        stage.setScene(new Scene(root));
        stage.setTitle(UI.APP_NAME);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setWidth(WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setResizable(true);
        stage.show();
    }
}
