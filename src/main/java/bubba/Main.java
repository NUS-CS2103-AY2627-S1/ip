package bubba;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays Bubba's JavaFX user interface.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 500;
    private static final double MINIMUM_WINDOW_WIDTH = 400;

    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowResource = Main.class.getResource("/view/MainWindow.fxml");
        if (mainWindowResource == null) {
            throw new IOException("MainWindow.fxml could not be found.");
        }

        FXMLLoader loader = new FXMLLoader(mainWindowResource);
        VBox root = loader.load();
        MainWindow controller = loader.getController();
        assert controller != null : "The FXML file must specify a MainWindow controller";
        controller.setBubba(new Bubba("./data/bubba.txt"));

        stage.setTitle("Bubba");
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
