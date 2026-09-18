package myriad.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import myriad.Myriad;

// AI-assisted: most of this file was written with Claude Code (Claude Opus),
// used by Ryan Chua, who reviewed and adapted the output.
/**
 * Displays the Myriad window. The layout itself lives in
 * {@code view/MainWindow.fxml} and the behaviour in {@link MainWindow}, so this class is
 * left with the wiring: load the layout, hand the controller a chatbot
 * session, and show the window.
 */
public class Main extends Application {

    /** Smallest size, in pixels, that the user can shrink the window to. */
    private static final double MIN_WINDOW_HEIGHT = 220.0;
    private static final double MIN_WINDOW_WIDTH = 300.0;

    /** The session behind the window, on the same data file as the console. */
    private final Myriad myriad = new Myriad(Myriad.DEFAULT_DATA_FILE, false);

    /**
     * Builds and shows the window when JavaFX has finished starting up.
     *
     * @param stage the window JavaFX supplies for this application.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();

            stage.setScene(new Scene(root));
            stage.setTitle("Myriad - your task painter");
            // The painted M doubles as the window's icon. Like the avatar,
            // a missing picture only leaves the default icon in place.
            Image icon = DialogBox.loadPicture("/images/DaMyriad.png");
            if (icon != null) {
                stage.getIcons().add(icon);
            }
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);

            controller.setMyriad(myriad);
            stage.show();
            controller.focusInput();
        } catch (IOException e) {
            // The layout file ships inside the application, so a failure here
            // means a broken build rather than anything the user can act on.
            // Throwing makes JavaFX abort the launch; returning quietly would
            // leave the program running with no window to show or close.
            throw new IllegalStateException("Could not load the main window layout /view/MainWindow.fxml", e);
        }
    }
}
