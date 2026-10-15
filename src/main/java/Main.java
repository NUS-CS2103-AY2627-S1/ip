import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ralph.Ralph;

/**
 * A GUI for Ralph using FXML.
 */
public class Main extends Application {

    private final Ralph ralph = new Ralph("data/ralph.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setTitle("Ralph");
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader
                .<MainWindow>getController()
                .setRalph(ralph); // inject the Ralph instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
