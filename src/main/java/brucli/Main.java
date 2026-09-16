package brucli;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the JavaFX entry point and constructs the application's initial window.
 */
public class Main extends Application {
    private final BruCliApp bruCliApp = new BruCliApp();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setBruCliApp(bruCliApp);

            stage.setTitle("BruCLI");
            stage.setMinHeight(600.0);
            stage.setMinWidth(400.0);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window.", e);
        }
    }
}
