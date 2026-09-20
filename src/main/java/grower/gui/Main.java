//@@author damithc-reused
//Code below and all Gui code is reused from: https://se-education.org/guides/tutorials/javaFxPart1.html
// With minor modifications
package grower.gui;

import java.io.IOException;

import grower.Grower;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the Grower graphical user interface.
 */
public class Main extends Application {
    private static final int MIN_WINDOW_HEIGHT = 220;
    private static final int MIN_WINDOW_WIDTH = 417;

    /** Shared application logic used throughout the lifetime of the GUI. */
    private final Grower grower = new Grower();

    /**
     * Loads the main window, supplies its Grower instance, and displays the primary stage.
     *
     * @param stage Primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();

            controller.setGrower(grower);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setTitle("Grow-er");
            // Tree by diamonjohn (Openclipart), released under CC0 1.0.
            // Source: https://openclipart.org/detail/170749/tree
            // Licence: https://creativecommons.org/publicdomain/zero/1.0/
            stage.getIcons().add(new Image(Main.class.getResource("/images/icon.png").toExternalForm()));
            stage.setScene(new Scene(mainWindow));
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the Grower interface.", e);
        }
    }
}
