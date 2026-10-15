package zeus.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import zeus.Zeus;

/** Displays the JavaFX interface for Zeus. */
public final class Main extends Application {
    /** Zeus instance that handles commands and persistent tasks. */
    private final Zeus zeus = new Zeus("data/zeus.txt");

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = loader.load();
        Scene scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.setTitle("Zeus");
        stage.setMinHeight(520.0);
        stage.setMinWidth(420.0);
        loader.<MainWindow>getController().setZeus(zeus);
        stage.show();
    }
}
