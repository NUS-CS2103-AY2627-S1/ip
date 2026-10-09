package sumo.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import sumo.Sumo;

/** Displays Sumo's JavaFX graphical interface. */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";

    private final Sumo sumo = new Sumo();

    /** Creates the application instance used by the JavaFX runtime. */
    public Main() {
    }

    /**
     * Loads the chat window and displays it on the primary stage.
     *
     * @param stage primary stage supplied by JavaFX.
     * @throws IOException if the window layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        AnchorPane mainWindow = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setSumo(sumo);

        stage.setScene(new Scene(mainWindow));
        stage.setTitle("Sumo");
        stage.setMinWidth(400.0);
        stage.setMinHeight(600.0);
        stage.show();
    }
}
