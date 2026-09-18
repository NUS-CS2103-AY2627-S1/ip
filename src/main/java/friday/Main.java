package friday;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Bootstraps Friday's JavaFX user interface.
 *
 * Layout details are kept in FXML so this class only coordinates application
 * startup and dependency injection.
 */
public class Main extends Application {
    /**
     * Loads the main view, injects the chatbot, and displays the window.
     *
     * @param stage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(Main.class.getResource("/view/friday.css").toExternalForm());
            MainWindow controller = loader.getController();
            controller.setChatbot(new Chatbot());
            stage.setTitle("FRIDAY");
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the main JavaFX view.", e);
        }
    }
}
