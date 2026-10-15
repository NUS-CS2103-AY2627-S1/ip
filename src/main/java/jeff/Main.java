package jeff;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jeff.gui.MainWindow;

/**
 * Displays the FXML-based Jeff GUI.
 */
public class Main extends Application {
    private final Jeff jeff = new Jeff("data/jeff.txt");

    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();

            MainWindow controller = fxmlLoader.getController();
            controller.setJeff(jeff);

            Scene scene = new Scene(mainLayout);
            stage.setTitle("Jeff");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
