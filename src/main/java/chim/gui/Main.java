package chim.gui;

import java.io.IOException;

import chim.Chim;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Chim using FXML.
 */
public class Main extends Application {

    private Chim chim = new Chim();

    /**
     * Loads the FXML layout and displays the primary window.
     *
     * @param stage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Chim");
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/icon.png")));
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setChim(chim);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
