package ev.gui;

import java.io.IOException;

import ev.EV;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application: it loads the window and hands it a chatbot to talk to.
 */
public class Main extends Application {

    private final EV ev = new EV(EV.DATA_FILE);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("EV");
            stage.setMinHeight(400);
            stage.setMinWidth(417);

            fxmlLoader.<MainWindow>getController().setEv(ev);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load /view/MainWindow.fxml", e);
        }
    }
}
