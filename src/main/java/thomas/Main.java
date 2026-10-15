package thomas;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Thomas using FXML.
 */
public class Main extends Application {

    private Thomas thomas = new Thomas();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);
            stage.setScene(scene);
            stage.getIcons().add(new Image(
                    Main.class.getResourceAsStream("/images/DaBot.jpg")));
            stage.setTitle("Thomas");
            stage.setMinWidth(420);
            stage.setMinHeight(520);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setThomas(thomas);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
