package echo.gui;

import java.io.IOException;

import echo.Echo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for Echo, loaded from {@code MainWindow.fxml}. Wraps the
 * same {@link Echo} instance the console UI uses, so both entry points
 * share the same task list, storage, and command handling.
 */
public class Main extends Application {
    private final Echo echo = new Echo("data/echo.txt");

    /**
     * Builds and shows the main window.
     *
     * @param stage The primary stage supplied by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Echo");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setEcho(echo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
