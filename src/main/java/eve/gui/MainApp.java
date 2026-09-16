package eve.gui;

import java.io.IOException;

import eve.Eve;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** The JavaFX application: loads the main window and wires it to an {@link Eve} instance. */
public class MainApp extends Application {
    private final Eve eve = new Eve("data/eve.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(MainApp.class.getResource("/view/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Eve");
            stage.setMinWidth(400);
            stage.setMinHeight(500);
            fxmlLoader.<MainWindow>getController().setEve(eve);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load MainWindow.fxml", e);
        }
    }
}
