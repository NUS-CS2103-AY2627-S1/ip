package wodan.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wodan.Wodan;

/**
 * A GUI for Wodan using FXML.
 */
public class Main extends Application {
    private final Wodan wodan = new Wodan();

    /**
     * Loads the main window and shows it on {@code stage}.
     *
     * @param stage Primary window provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/wodan.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Wodan");
            stage.setResizable(true);
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/wodan.png")));
            fxmlLoader.<MainWindow>getController().setWodan(wodan);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load the Wodan window.", e);
        }
    }
}
