import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** JavaFX entry point: loads the FXML-defined chat window and wires it to a {@link MaggiGorengAyamBot}. */
public class Main extends Application {

    private MaggiGorengAyamBot bot = new MaggiGorengAyamBot();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Maggi Goreng Ayam");  // the name
            stage.setResizable(true);
            stage.setMinHeight(250);
            stage.setMinWidth(340);
            fxmlLoader.<MainWindow>getController().setBot(bot);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load view/MainWindow.fxml", e);
        }
    }
}
