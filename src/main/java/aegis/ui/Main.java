package aegis.ui;

import java.io.IOException;

import aegis.Aegis;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Aegis using FXML.
 */
public class Main extends Application {

    private static final int MIN_WINDOW_HEIGHT = 220;
    private static final int MIN_WINDOW_WIDTH = 417;
    private Aegis aegis = new Aegis("data/aegis.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Aegis");
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setAegis(aegis); // inject the Aegis instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
