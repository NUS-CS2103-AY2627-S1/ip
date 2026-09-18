package tally.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tally.Tally;

/** The window Tally runs in. */
public class Main extends Application {
    /** How small the window may be made before the conversation stops fitting in it. */
    private static final double MIN_WINDOW_HEIGHT = 420.0;
    private static final double MIN_WINDOW_WIDTH = 420.0;

    private final Tally tally = new Tally(false);

    /**
     * Builds the window and shows it.
     *
     * @param stage the window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    Main.class.getResource("/view/tally.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Tally");
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            loader.<MainWindow>getController().setTally(tally);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("The window could not be built.", exception);
        }
    }
}
