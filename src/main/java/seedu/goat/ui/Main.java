package seedu.goat.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import seedu.goat.Goat;

/**
 * Builds the chat window and hands it the chatbot to talk to.
 * <p>
 * The layout lives in {@code MainWindow.fxml} rather than in Java here, so that the
 * arrangement of the window can be changed without recompiling the logic behind it.
 */
public class Main extends Application {

    /** The chatbot behind the window, using the same save file as the text interface. */
    private final Goat goat = new Goat(Goat.DEFAULT_SAVE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setGoat(goat);

            stage.setTitle("GOAT");
            stage.setScene(new Scene(root));

            // Below this size the send button starts crowding out the text field.
            stage.setMinWidth(300.0);
            stage.setMinHeight(400.0);

            stage.show();
        } catch (IOException e) {
            // There is no window in which to report a window that failed to load, so the
            // trace goes to the console instead of vanishing.
            e.printStackTrace();
        }
    }
}
