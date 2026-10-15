package tangent.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tangent.Tangent;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private final Tangent tangent = new Tangent("data/tangent.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Image icon = new Image(this.getClass().getResourceAsStream("/images/logo.png"));
            Scene scene = new Scene(ap, 460, 700);
            stage.setScene(scene);
            stage.setTitle("Tangent");
            stage.setMinWidth(360);
            stage.setMinHeight(480);
            stage.getIcons().add(icon);
            fxmlLoader.<MainWindow>getController().setTangent(tangent);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
