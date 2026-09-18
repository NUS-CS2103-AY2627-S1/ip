package benjamin.gui;

import java.io.IOException;

import benjamin.Benjamin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX application that shows the chat window.
 */
public class Main extends Application {
    private final Benjamin benjamin = new Benjamin();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    Main.class.getResource("/view/benjamin.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Benjamin");
            // Small enough to sit beside other windows, but not so small that
            // the input row and a reply cannot both be seen.
            stage.setMinWidth(320.0);
            stage.setMinHeight(400.0);

            fxmlLoader.<MainWindow>getController().setBenjamin(benjamin);
            stage.show();
        } catch (IOException exception) {
            System.err.println("Could not load the chat window: " + exception.getMessage());
        }
    }
}
