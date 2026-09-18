package waffles;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Builds and displays the Waffles JavaFX window.
 */
public class Main extends Application {
    /** The chatbot model shared by the GUI controller. */
    private final Waffles waffles;

    /** Creates the JavaFX application and its chatbot model. */
    public Main() {
        waffles = new Waffles();
    }

    /**
     * Loads the main FXML view and displays it in the primary stage.
     *
     * @param stage the primary JavaFX window
     * @throws IOException when the FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setWaffles(waffles);

        Scene scene = new Scene(root);
        stage.setTitle("Waffles");
        stage.setScene(scene);
        stage.setMinWidth(440);
        stage.setMinHeight(560);
        stage.setWidth(520);
        stage.setHeight(720);
        stage.setResizable(true);
        stage.show();
    }
}
