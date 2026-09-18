package hermes;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Opens the window Hermes is shown in. */
public class Main extends Application {

    /** The smallest the window may be dragged, in pixels. */
    private static final int MIN_HEIGHT = 220;
    private static final int MIN_WIDTH = 417;

    private final Hermes hermes = new Hermes();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(MIN_HEIGHT);
            stage.setMinWidth(MIN_WIDTH);
            fxmlLoader.<MainWindow>getController().setHermes(hermes);
            stage.setTitle("Hermes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
