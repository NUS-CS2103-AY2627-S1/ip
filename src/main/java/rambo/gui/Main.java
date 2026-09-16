package rambo.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rambo.Rambo;
import rambo.ui.Constants;

/**
 * A GUI for Rambo using FXML.
 */
public class Main extends Application {
    private static final int MIN_WINDOW_HEIGHT = 420;
    private static final int MIN_WINDOW_WIDTH = 360;

    private final Rambo rambo = new Rambo();

    /**
     * Loads the chat window and displays it on the primary stage.
     *
     * @param stage primary application window
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            scene.getStylesheets().addAll(
                    Main.class.getResource("/css/main.css").toExternalForm(),
                    Main.class.getResource("/css/dialog-box.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(Constants.BOT_NAME + " | Mission control");
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setRambo(rambo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
