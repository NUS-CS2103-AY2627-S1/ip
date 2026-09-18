package rem.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rem.Rem;

/**
 * Loads the FXML chat window and connects it to Rem's saved task list.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(loader.load());
        MainWindow controller = loader.getController();
        controller.setRem(new Rem("data/rem.txt"));
        stage.setTitle("RemBot");
        stage.setScene(scene);
        stage.setMinWidth(380);
        stage.setMinHeight(400);
        stage.show();
        controller.showRecoveryDialog(stage);
    }
}
