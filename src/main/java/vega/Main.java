package vega;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts Vega's JavaFX user interface. */
public class Main extends Application {
    private final Vega vega = new Vega("data/vega.txt");

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        MainWindow controller = loader.getController();
        controller.setVega(vega);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/view/vega.css").toExternalForm());
        stage.setTitle("Vega");
        stage.setMinWidth(400);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }
}
