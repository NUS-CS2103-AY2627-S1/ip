package eva;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Eva's JavaFX interface.
 */
public class Main extends Application {
    private final Eva eva = new Eva("data/eva.txt");

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setEva(eva);

        stage.setScene(new Scene(mainLayout));
        stage.setTitle("Eva");
        stage.setResizable(false);
        stage.show();
    }
}
