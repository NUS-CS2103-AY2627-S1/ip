package downtowngurl;

import java.io.IOException;

import downtowngurl.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Downtown Gurl using FXML.
 */
public class Main extends Application {
    private final DowntownGurl downtownGurl = new DowntownGurl();

    /**
     * Loads the main window layout and shows it on the primary stage.
     *
     * @param stage primary window provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setTitle("Downtown Gurl");
            stage.setScene(scene);
            stage.setMinWidth(400);
            stage.setMinHeight(200);
            fxmlLoader.<MainWindow>getController().setDowntownGurl(downtownGurl);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load MainWindow.fxml.", e);
        }
    }
}
