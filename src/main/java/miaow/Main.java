package miaow;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Main
 */
public class Main {
    private static final double WINDOW_WIDTH = 400.0;
    private static final double WINDOW_HEIGHT = 600.0;

    private final Miaow miaow = new Miaow();

    /**
     * starts
     * @param stage
     */
    public void start(Stage stage) {
        URL mainWindow = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "Missing main-window FXML resource");
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindow);

        try {
            AnchorPane mainLayout = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setMiaow(miaow);

            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Miaow");
            stage.setResizable(false);
            stage.setMinHeight(WINDOW_HEIGHT);
            stage.setMinWidth(WINDOW_WIDTH);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Can't load main window.", e);
        }
    }
}
