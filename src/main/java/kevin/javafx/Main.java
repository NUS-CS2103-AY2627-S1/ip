package kevin.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import kevin.Kevin;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Kevin kevin = new Kevin("data/tasks.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();

            Scene scene = new Scene(ap);
            stage.setTitle("Kevin");
            stage.setScene(scene);
            
            fxmlLoader.<MainWindow>getController().setKevin(kevin);  // inject the Kevin instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



