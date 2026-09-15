package luigibot.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import luigibot.LuigiBot;

/**
 * Displays the LuigiBot JavaFX window.
 */
public class Main extends Application {
    private static final String TASK_FILE_PATH = "data/luigibot.txt";

    /**
     * Creates the JavaFX application instance.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setLuigiBot(new LuigiBot(TASK_FILE_PATH));
        Scene scene = new Scene(mainLayout);

        stage.setTitle("LuigiBot");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}
