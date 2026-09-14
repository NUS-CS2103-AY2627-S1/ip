package oreo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Creates Oreo's main JavaFX window. */
public class Main extends Application {
    /** Displays the application window and connects it to Oreo's command logic. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/view/oreo.css").toExternalForm());

        stage.setTitle("Oreo");
        stage.setResizable(true);
        stage.setMinWidth(520);
        stage.setMinHeight(600);
        stage.setScene(scene);
        loader.<MainWindow>getController().setOreo(new Oreo("data/Oreo.txt"));
        stage.show();
    }
}
