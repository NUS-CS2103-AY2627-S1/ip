package serangooner.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import serangooner.Serangooner;

/**
 * Shows the chatbot in a window.
 * The window itself is described in {@code MainWindow.fxml}; all this class
 * does is put that description on a stage and hand the controller behind it
 * the chatbot whose answers it is to show.
 */
public class Main extends Application {
    private static final String WINDOW_TITLE = "serangooner";
    private static final int MINIMUM_WIDTH = 417;
    private static final int MINIMUM_HEIGHT = 220;

    private final Serangooner chatbot = new Serangooner();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow window = fxmlLoader.getController();
            window.setChatbot(chatbot);

            stage.setScene(new Scene(root));
            stage.setTitle(WINDOW_TITLE);
            stage.setMinWidth(MINIMUM_WIDTH);
            stage.setMinHeight(MINIMUM_HEIGHT);
            stage.show();

            window.showGreeting();
        } catch (IOException exception) {
            // A window that cannot be built leaves nothing worth carrying on with.
            throw new IllegalStateException("cannot load the chat window", exception);
        }
    }
}
