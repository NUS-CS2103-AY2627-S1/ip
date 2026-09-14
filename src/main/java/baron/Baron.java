// javadocs created by AI

package baron;

import baron.data.BaronState;
import baron.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Baron task management application.
 * The program starts a command loop that reads user input, dispatches commands,
 * and manages the application state until the user exits.
 */
public class Baron extends Application {
    /**
     * Starts the Baron application and processes user commands until termination.
     *
     * @throws Exception if the main window cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws Exception {
        BaronState.init();
        FXMLLoader loader = new FXMLLoader(Baron.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow controller = loader.getController();
        assert root != null : "MainWindow FXML must load a root node";
        assert controller != null : "MainWindow FXML must provide its controller";
        controller.showWelcome();
        stage.setTitle("Baron Task Manager");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.show();
        controller.applyBackground();
    }

    /**
     * The main method that serves as the entry point for the Baron application.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
