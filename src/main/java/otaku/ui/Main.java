package otaku.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import otaku.Otaku;

/** JavaFX entry point for Otaku's graphical interface. */
public class Main extends Application {
    private final Otaku otaku = new Otaku();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setOtaku(otaku);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/view/Theme.css").toExternalForm());
        stage.setTitle("Otaku");
        stage.setMinWidth(520);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }
}
