import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import kiwi.Kiwi;

/**
 * A GUI for Kiwi using FXML.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_WIDTH = 320;
    private static final double MIN_WINDOW_HEIGHT = 420;

    private Kiwi kiwi = new Kiwi(Kiwi.DEFAULT_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Kiwi");
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setKiwi(kiwi);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
