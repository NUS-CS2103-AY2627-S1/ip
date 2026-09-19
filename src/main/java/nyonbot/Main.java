package nyonbot;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nyonbot.controller.MainWindow;

/**
 * Creates and launches the GUI for NyonBot.
 */
public class Main extends Application {

    private NyonBot nyonBot = new NyonBot();
    private final int minHeight = 600;
    private final int minWidth = 480;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(minHeight);
            stage.setMinWidth(minWidth);
            fxmlLoader.<MainWindow>getController().setNyonBot(nyonBot);
            stage.setTitle("Nyonbot");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
