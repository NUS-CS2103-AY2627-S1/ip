package percy;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX entry point. Builds the main window from FXML and hands its
 * controller a {@link Percy} instance to talk to.
 */
public class Main extends Application {
    private final Percy percy = new Percy("./data/percy.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Percy");
            stage.setMinWidth(400.0);
            stage.setMinHeight(600.0);
            fxmlLoader.<MainWindow>getController().setPercy(percy);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
