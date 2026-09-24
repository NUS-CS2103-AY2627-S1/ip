package sage;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX frontend for Sage's command-processing service. */
public class SageGui extends Application {
    /** Creates the JavaFX frontend. */
    public SageGui() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SageGui.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        loader.<MainWindow>getController().setSage(new Sage());
        stage.setTitle("Sage");
        stage.setScene(new Scene(root, 560, 420));
        stage.show();
    }
}
