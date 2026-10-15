package lily;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lily.ui.MainWindow;

/** Starts Lily's JavaFX window. */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setLily(new Lily("data/lily.txt"));
        stage.setTitle("Lily");
        stage.setResizable(true);
        stage.setMinWidth(360);
        stage.setMinHeight(400);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/view/lily.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
