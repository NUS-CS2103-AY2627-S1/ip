package bot;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the FXML-based JavaFX interface for the chatbot.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 320.0;
    private static final double MINIMUM_WINDOW_WIDTH = 360.0;

    private final Bot bot = new Bot();

    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setBot(bot);

        Scene scene = new Scene(mainLayout);
        stage.setTitle("Bot");
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}
