package penny;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import penny.ui.MainWindow;

/**
 * Provides a GUI for Penny using FXML.
 */
public class Main extends Application {

    private final Penny penny = new Penny("data", "penny.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Penny // SP//dr Tactical HUD");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/DaPenny.png")));
            fxmlLoader.<MainWindow>getController().setPenny(penny);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
