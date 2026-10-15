package coco;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A JavaFX App that loads the main window and wires it to a Coco instance.
 */
public class Main extends Application {
    private final Coco coco = new Coco("data/coco.txt");

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

        stage.setTitle("Coco");
        stage.setScene(scene);
        // JavaFX stages are resizable by default; set only a floor so the
        // window can't be shrunk small enough to clip the input row.
        stage.setMinWidth(320);
        stage.setMinHeight(420);
        fxmlLoader.<MainWindow>getController().setCoco(coco);
        stage.show();
    }
}
