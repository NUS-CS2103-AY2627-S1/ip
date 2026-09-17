package nova.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nova.Nova;

/**
 * Configures and displays Nova's JavaFX window.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_WIDTH = 440.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 540.0;

    /**
     * Loads Nova's main window and connects it to the chatbot.
     *
     * @param stage primary application window.
     * @throws IOException if the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        controller.setNova(new Nova("data/nova.txt"));

        stage.setScene(new Scene(mainLayout));
        stage.setTitle("Nova");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.show();
        controller.focusInput();
    }
}
