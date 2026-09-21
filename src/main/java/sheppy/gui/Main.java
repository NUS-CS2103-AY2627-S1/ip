package sheppy.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sheppy.Sheppy;

/** Configures and displays Sheppy's main JavaFX window. */
public class Main extends Application {
    /** Creates the JavaFX application entry point. */
    public Main() {
    }

    /**
     * Loads the main window and connects it to the chatbot.
     *
     * @param stage the window supplied by JavaFX
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow controller = loader.getController();
        controller.setSheppy(new Sheppy());

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sheppy");
        stage.setMinWidth(520);
        stage.setMinHeight(480);
        stage.show();
    }
}
