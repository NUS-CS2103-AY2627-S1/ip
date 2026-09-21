package ted;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The JavaFX application behind Ted's window.
 * <p>
 * Its only job is to load the window described in {@code MainWindow.fxml}, hand
 * the controller a {@link Ted} to talk to, and show the result. The window's
 * appearance lives in the FXML and its behavior in {@link MainWindow}. Adapted
 * from the SE-EDU JavaFX tutorial
 * (https://se-education.org/guides/tutorials/javaFx.html).
 */
public class Main extends Application {
    /** The chatbot answering in this window. */
    private final Ted ted = new Ted();

    /**
     * Creates the application. JavaFX calls this itself, before
     * {@link #start(Stage)}, which is why it must be public and take no arguments.
     */
    public Main() {
    }

    /**
     * Loads Ted's main window, connects it to the chatbot, and displays it.
     * If the window cannot be loaded, reports the error to standard error and
     * closes the app.
     *
     * @param stage the primary window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Ted");
            // Ted's picture sits beside the window title, and in the task bar
            // on systems that have one, so the window is easy to pick out.
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/DaTed.png")));
            // The layout stretches to any size, but below these the window has
            // room for too little of the conversation to be useful.
            stage.setMinHeight(300);
            stage.setMinWidth(320);
            fxmlLoader.<MainWindow>getController().setTed(ted);
            stage.show();
        } catch (IOException e) {
            // Without its window there is nothing left for the app to do, so the
            // failure is reported and the app closes rather than running unseen.
            e.printStackTrace();
            Platform.exit();
        }
    }
}
