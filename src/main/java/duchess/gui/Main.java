package duchess.gui;

import java.io.IOException;

import duchess.Duchess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** The JavaFX application that hosts Duchess's chat window. */
public class Main extends Application {
    /** The initial width of the chat window. */
    private static final double WINDOW_WIDTH = 720.0;

    /** The initial height of the chat window. */
    private static final double WINDOW_HEIGHT = 780.0;

    /** The smallest width that keeps the command composer usable. */
    private static final double MIN_WINDOW_WIDTH = 420.0;

    /** The smallest height that keeps the conversation and composer visible. */
    private static final double MIN_WINDOW_HEIGHT = 560.0;

    /**
     * Loads the FXML view, injects the Duchess command processor, and shows the window.
     *
     * @param stage the primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox root = fxmlLoader.load();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            MainWindow controller = fxmlLoader.getController();
            controller.setDuchess(new Duchess());

            stage.setTitle("Duchess ✦ Squad Task Desk");
            scene.widthProperty().addListener((observable, oldWidth, newWidth) -> {
                double fontSize = Math.clamp(newWidth.doubleValue() / 50, 14, 18);
                root.setStyle("-fx-font-size: " + fontSize + "px;");
            });
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | NullPointerException exception) {
            throw new IllegalStateException("Unable to load the Duchess GUI resources.", exception);
        }
    }
}
