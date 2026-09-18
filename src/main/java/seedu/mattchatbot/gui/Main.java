package seedu.mattchatbot.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seedu.mattchatbot.MattChatBot;

/**
 * The JavaFX application itself: builds the window and hands it a chatbot to
 * talk to.
 * <p>
 * Kept apart from {@link seedu.mattchatbot.Launcher}, which is the class the
 * JAR actually starts from.
 */
public class Main extends Application {

    /** The chatbot the window sends its input to. */
    private final MattChatBot chatBot = MattChatBot.forGui();

    /** Creates the application. JavaFX calls this itself when starting up. */
    public Main() {
    }

    /**
     * Builds and shows the window.
     *
     * @param stage the window JavaFX provides
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/view/main.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Winston");
            stage.setMinWidth(360);
            stage.setMinHeight(480);

            loader.<MainWindow>getController().setChatBot(chatBot);
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged alongside the code, so a failure here means
            // a broken build rather than anything the user can act on.
            throw new IllegalStateException("Could not load the chatbot's window.", e);
        }
    }
}
