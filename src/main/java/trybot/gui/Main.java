package trybot.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import trybot.TryBot;

/** JavaFX application that displays the TryBot conversation. */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            MainWindow controller = loader.getController();
            controller.setTryBot(new TryBot());

            stage.setTitle("TryBot");
            stage.setScene(new Scene(root));
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            stage.setResizable(true);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the TryBot window.", exception);
        }
    }
}
