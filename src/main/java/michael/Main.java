package michael;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

/** Starts Michael's JavaFX application and loads its FXML layout. */
public class Main extends Application {
    private final Michael michael = new Michael("data/michael.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setMichael(michael);
            stage.setTitle("Michael");
            stage.setResizable(false);
            stage.setScene(new Scene(mainWindow));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
