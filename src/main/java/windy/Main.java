package windy;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import windy.ui.MainWindow;

/**
 * Starts the JavaFX GUI for Windy.
 */
public class Main extends Application {

    private final Windy windy = new Windy();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "Main window FXML resource not found"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);
        stage.setTitle("Windy");
        stage.setResizable(true);
        stage.setMinHeight(220.0);
        stage.setMinWidth(417.0);
        stage.setScene(scene);

        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setWindy(windy);
        stage.show();
    }
}
