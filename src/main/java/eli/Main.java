package eli;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Displays Eli's JavaFX interface using its FXML view. */
public class Main extends Application {
    private final Eli eli = new Eli();

    /** Loads and displays the main Eli window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.setTitle("Eli");
        stage.setMinHeight(400.0);
        stage.setMinWidth(420.0);
        fxmlLoader.<MainWindow>getController().setEli(eli);
        stage.show();
    }
}
