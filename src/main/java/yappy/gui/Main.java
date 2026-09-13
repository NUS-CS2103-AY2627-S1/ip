package yappy.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import yappy.Yappy;

/**
 * Loads and displays Yappy's JavaFX interface.
 */
public class Main extends Application {
    private final Yappy yappy = new Yappy();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.setTitle("Yappy");
        stage.setMinHeight(220);
        stage.setMinWidth(417);
        fxmlLoader.<MainWindow>getController().setYappy(yappy);
        stage.show();
    }
}
