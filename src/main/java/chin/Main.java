package chin;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Tutorial Part 5: root JavaFX application that loads the chat UI from FXML.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Chin");
        stage.setScene(scene);
        stage.show();
    }
}
