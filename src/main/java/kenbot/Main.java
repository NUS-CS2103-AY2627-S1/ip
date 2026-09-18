package kenbot;

import java.io.IOException;
import java.io.UncheckedIOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Kenbot using FXML.
 *
 * <p>Extending {@link Application} is what makes this a JavaFX program: the
 * toolkit starts itself up and then calls {@link #start(Stage)} once the window
 * system is ready. Nothing here is called directly.</p>
 */
public class Main extends Application {

    private Kenbot kenbot = new Kenbot("data/Kenbot.txt");

    /**
     * Creates the application. JavaFX calls this itself before
     * {@link #start(Stage)}, which is why it takes no arguments.
     */
    public Main() {
    }

    /**
     * Builds and shows the chat window from its FXML description.
     *
     * @param stage the window JavaFX created for this application
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();

            stage.setTitle("Kenbot");
            stage.setScene(new Scene(root));

            // Floors rather than fixed sizes: the window is free to grow, but
            // cannot be dragged smaller than the input row and a usable slice
            // of the conversation.
            stage.setMinWidth(417.0);
            stage.setMinHeight(220.0);

            // The controller is created by the loader, so the only way to hand
            // it the chatbot is to ask for it once loading is done.
            fxmlLoader.<MainWindow>getController().setKenbot(kenbot);

            stage.show();
        } catch (IOException e) {
            // Same reasoning as in DialogBox: the FXML ships inside the JAR, so
            // this cannot be recovered from. Printing and returning would leave
            // start() finishing normally without ever calling stage.show(), so
            // the program would sit there running with no window and no sign of
            // what went wrong.
            throw new UncheckedIOException("Could not load /view/MainWindow.fxml", e);
        }
    }
}
