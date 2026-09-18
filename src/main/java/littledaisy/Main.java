package littledaisy;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import littledaisy.ui.MainWindow;

/** Loads and displays littleDaisy's JavaFX interface. */
public class Main extends Application {
    private final LittleDaisy littleDaisy = new LittleDaisy();

    /** Creates the main window and injects the application logic into its controller. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setLittleDaisy(littleDaisy);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("littleDaisy");
        stage.setMinWidth(440);
        stage.setMinHeight(620);
        stage.show();
    }
}
