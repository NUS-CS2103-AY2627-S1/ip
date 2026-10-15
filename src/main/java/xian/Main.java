package xian;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts the JavaFX user interface for the Xian task management application.
 */
public class Main extends Application {

    /**
     * Creates and displays the main Xian window from its FXML layout.
     *
     * @param stage The primary JavaFX stage provided by the application.
     * @throws IOException If the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        MainWindow mainWindow = fxmlLoader.getController();
        Xian xian = new Xian("data/xian.txt");
        mainWindow.setXian(xian);

        stage.setTitle("Xian");
        stage.setScene(scene);
        stage.setMinWidth(360);
        stage.setMinHeight(400);
        stage.show();
    }
}
