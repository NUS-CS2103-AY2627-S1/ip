package morgan;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import morgan.gui.MainWindow;

/**
 * A GUI for Morgan using FXML.
 */
public class Main extends Application {

    private final Morgan morgan = new Morgan("./data/morgan.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Morgan");

            fxmlLoader.<MainWindow>getController().setMorgan(morgan);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
