package orion;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import orion.gui.MainWindow;

/**
 * A GUI for Orion using FXML.
 */
public class Main extends Application {
    private Orion orion = new Orion("./data/orion.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            scene.getStylesheets().add(Main.class.getResource("/view/main.css").toExternalForm());
            stage.setMinHeight(220);
            stage.setMinWidth(440);
            stage.setTitle("Orion");
            fxmlLoader.<MainWindow>getController().setOrion(orion);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
