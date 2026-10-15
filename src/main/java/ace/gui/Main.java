package ace.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX GUI. Loads the chat window's layout from
 * {@code MainWindow.fxml} and injects an {@link AceCore} instance into its
 * controller, so the window can run Ace's real command logic.
 */
public class Main extends Application {
    private final AceCore aceCore = new AceCore();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();

            Scene scene = new Scene(mainLayout);
            stage.setScene(scene);

            stage.setTitle("Ace");
            stage.setMinHeight(600.0);
            stage.setMinWidth(400.0);

            MainWindow controller = fxmlLoader.getController();
            controller.setAceCore(aceCore);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MainWindow.fxml", e);
        }
    }
}
