package shrek;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the Shrek JavaFX application.
 *
 * <p>
 * The visual layout is kept in FXML and the interaction logic is kept in
 * {@link MainWindow}, following the view/controller separation from the
 * JavaFX tutorial.
 * </p>
 */
public class Main extends Application {

    private final Shrek shrek = new Shrek();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setShrek(shrek);

            Scene scene = new Scene(root);
            stage.setTitle("Shrek Chatbot");
            stage.setResizable(true);
            stage.setMinWidth(500);
            stage.setMinHeight(600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Unable to start the Shrek GUI.", e);
        }
    }
}
