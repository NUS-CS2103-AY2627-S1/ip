package aster.gui;

import java.io.IOException;
import java.io.UncheckedIOException;

import aster.Aster;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Aster's graphical interface: a window in which the user types commands and Aster
 * answers in dialog boxes.
 *
 * <p>The window's layout is described in {@code /view/MainWindow.fxml}, and what the
 * window does is handled by {@link MainWindow}. This class only builds the window
 * around one Aster.
 */
public class Main extends Application {
    // The smallest window in which the header, the message field, the Send button and a
    // few replies all remain usable.
    private static final double MIN_HEIGHT = 360;
    private static final double MIN_WIDTH = 417;

    private final Aster aster = new Aster();

    /**
     * Creates the application; JavaFX calls this before {@link #start(Stage)}.
     */
    public Main() {
    }

    /**
     * Shows the chat window with Aster's opening reply in it.
     *
     * @param stage the window JavaFX provides.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Aster");
            stage.setMinHeight(MIN_HEIGHT);
            stage.setMinWidth(MIN_WIDTH);
            fxmlLoader.<MainWindow>getController().setAster(aster);
            stage.show();
        } catch (IOException e) {
            // The layout is packaged with Aster, so failing to load it is a fault in the
            // build rather than something the user can correct.
            throw new UncheckedIOException("Could not load the main window layout.", e);
        }
    }
}
