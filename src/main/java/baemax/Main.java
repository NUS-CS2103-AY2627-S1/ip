package baemax;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * The JavaFX application: loads the main window and connects it to a
 * {@link Baemax} instance.
 */
public class Main extends Application {
    private final Baemax baemax = new Baemax("data/baemax.txt");

    @Override
    public void start(Stage stage) {
        try {
            Font.loadFont(Main.class.getResourceAsStream("/fonts/VT323-Regular.ttf"), 20);
            Font.loadFont(Main.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 11);

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Baemax");
            stage.setMinHeight(220.0);
            stage.setMinWidth(417.0);
            fxmlLoader.<MainWindow>getController().setBaemax(baemax);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
