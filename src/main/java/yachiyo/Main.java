package yachiyo;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import yachiyo.ui.MainWindow;

/**
 * Displays the JavaFX user interface for Yachiyo using FXML.
 */
public class Main extends Application {
    private final Yachiyo yachiyo = new Yachiyo();

    /**
     * Loads and displays the chat interface.
     *
     * @param stage primary window supplied by JavaFX.
     * @throws IOException if the FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "Main window FXML resource not found."
        ));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setYachiyo(yachiyo);

        Scene scene = new Scene(mainLayout);
        stage.setTitle("Yachiyo");
        stage.setMinHeight(220.0);
        stage.setMinWidth(417.0);
        stage.setScene(scene);
        stage.show();
    }
}
