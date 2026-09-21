package twizzy.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import twizzy.Twizzy;

/** Provides Twizzy's JavaFX application window. */
public class TwizzyGui extends Application {
    /** Creates and displays the primary Twizzy window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(TwizzyGui.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        MainWindow controller = loader.getController();
        controller.setTwizzy(new Twizzy());

        stage.setTitle("Twizzy");
        stage.setMinWidth(480);
        stage.setMinHeight(620);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
