package joebiden.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import joebiden.JoeBiden;

/**
 * A GUI for Joe Biden using FXML.
 */
public class Main extends Application {

    private final JoeBiden joeBiden = new JoeBiden();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource(
                            "/view/MainWindow.fxml"
                    ));

            AnchorPane anchorPane = fxmlLoader.load();

            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);

            MainWindow controller = fxmlLoader.getController();
            controller.setJoeBiden(joeBiden);

            stage.setTitle("Joe Biden");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
