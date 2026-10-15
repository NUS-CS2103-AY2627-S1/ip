package sylveon;

import javafx.application.Application;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the graphical user interface for Sylveon.
 */
public class Main extends Application {

    /**
     * Creates and displays the Sylveon window.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = loader.load();

        Sylveon sylveon = new Sylveon();
        MainWindow controller = loader.getController();
        controller.setSylveon(sylveon);

        stage.setTitle("Sylveon Task Manager");
        stage.setMinWidth(417);
        stage.setMinHeight(220);
        stage.setScene(new Scene(mainLayout));
        stage.show();
    }
}
