package peter.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import peter.Peter;

/**
 * Builds the application window and connects it to the chatbot.
 */
public class Main extends Application {

    /** Same relative path the console entry point uses, so both share a data file. */
    private static final String DATA_FILE_PATH = "data/peter.txt";

    private final Peter peter = new Peter(DATA_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();

            // The loader creates the controller with a no-arg constructor, so the
            // chatbot has to be handed over afterwards rather than through it.
            loader.<MainWindow>getController().setPeter(peter);

            stage.setScene(new Scene(root));
            stage.setTitle("Peter - Friendly Neighborhood Task Buddy");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/SpiderIcon.png")));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
