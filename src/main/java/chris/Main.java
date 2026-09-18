package chris;

import java.io.IOException;

import chris.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the JavaFX interface for Chris.
 */
public class Main extends Application {
    private final Chris chris = new Chris();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        stage.setTitle("Chris");
        stage.setMinHeight(300.0);
        stage.setMinWidth(400.0);
        stage.setScene(scene);
        fxmlLoader.<MainWindow>getController().setChris(chris);
        stage.show();
    }
}
