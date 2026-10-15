package summer;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import summer.ui.MainWindow;

/**
 * The JavaFX {@link Application} for Summer. Loads the main window from FXML and
 * hands it a {@link Summer} instance to talk to.
 */
public class Main extends Application {
    private final Summer summer = new Summer();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Summer");
            stage.setMinWidth(320);
            stage.setMinHeight(400);
            fxmlLoader.<MainWindow>getController().setSummer(this.summer);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
