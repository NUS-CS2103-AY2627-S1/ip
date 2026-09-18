package kopi;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts the Kopi JavaFX interface. */
public class Main extends Application {
    private final Kopi kopi = new Kopi(Path.of("data", "kopi.txt"));

    /** Shows the application window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = loader.load();
        loader.<MainWindow>getController().setKopi(kopi);

        stage.setTitle("Kopi");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Kopi.png")));
        stage.setMinHeight(360);
        stage.setMinWidth(420);
        stage.setScene(new Scene(mainWindow));
        stage.show();
    }
}
