package tblade.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tblade.TBlade;

/**
 * A GUI for TBlade using FXML.
 */
public class Main extends Application {
    private final TBlade tblade = new TBlade(TBlade.DEFAULT_DATA_FILE_PATH);

    /**
     * Loads MainWindow.fxml, injects the TBlade instance into its controller, and shows the stage.
     *
     * @param stage the primary stage provided by JavaFX
     * @throws IOException if {@code MainWindow.fxml} cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane ap = fxmlLoader.load();
        Scene scene = new Scene(ap);
        scene.getStylesheets().add(Main.class.getResource("/view/technoblade.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("TBlade — Blood God of Task Lists");
        fxmlLoader.<MainWindow>getController().setTblade(tblade);
        stage.show();
    }
}
