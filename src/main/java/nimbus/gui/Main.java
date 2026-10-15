package nimbus.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nimbus.Nimbus;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Nimbus nimbus = new Nimbus();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setNimbus(nimbus); // inject the Duke instance
            Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
            stage.getIcons().add(userImage);
            stage.setTitle("Nimbus");
            stage.setOnCloseRequest(event -> nimbus.saveTaskList());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
