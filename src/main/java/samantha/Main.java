package samantha;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import samantha.ui.MainWindow;

/**
 * Starts the Samantha JavaFX user interface.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_WIDTH = 520;
    private static final double MIN_WINDOW_HEIGHT = 680;

    /**
     * Configures and displays Samantha's initial JavaFX stage.
     *
     * @param stage primary JavaFX stage
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();

        Scene scene = new Scene(mainWindow);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setTitle("Samantha");
        stage.setScene(scene);
        stage.show();
        controller.setSamantha(new Samantha());
    }
}
