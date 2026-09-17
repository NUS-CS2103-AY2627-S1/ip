package glennon.gui;

import java.io.IOException;

import glennon.Glennon;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Loads Glennon's FXML view and connects it to the application logic.
 */
public class Main extends Application {
    private final Glennon glennon = new Glennon();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setGlennon(glennon);
        stage.setTitle("Glennon | Mission Control");
        stage.setScene(new Scene(root));
        stage.setMinWidth(380);
        stage.setMinHeight(360);
        stage.show();
    }
}
