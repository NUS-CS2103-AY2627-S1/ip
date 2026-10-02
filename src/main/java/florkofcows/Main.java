package florkofcows;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * A GUI for FlorkOfCows using FXML.
 */
public class Main extends Application {

    private FlorkOfCows florkOfCows = new FlorkOfCows();

    @Override
    public void start(Stage stage) {
        try {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double width = screenBounds.getWidth() / 2.0;
            double height = screenBounds.getHeight() * 0.75;

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap, width, height);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX((screenBounds.getWidth() - width) / 2);
            stage.setY((screenBounds.getHeight() - height) / 2);
            fxmlLoader.<MainWindow>getController().setFlorkOfCows(florkOfCows); // inject the FlorkOfCows instance
            stage.setTitle("FlorkOfCows");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
