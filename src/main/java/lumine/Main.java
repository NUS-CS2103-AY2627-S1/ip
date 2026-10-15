package lumine;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lumine.ui.MainWindow;

/**
 * Launches Lumine's JavaFX graphical user interface.
 */
public class Main extends Application {

    /**
     * {@inheritDoc}
     * Loads and displays Lumine's main conversation window.
     *
     * @param stage the primary window supplied by JavaFX.
     * @throws IOException if the main window layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Lumine lumine = new Lumine("data/lumine.txt");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        Scene scene = new Scene(mainWindow);

        stage.setTitle("Lumine");
        stage.getIcons().add(new Image(Main.class.getResource("/images/lumine_icon.jpg").toExternalForm()));
        stage.setMinHeight(300);
        stage.setMinWidth(400);
        stage.setScene(scene);
        fxmlLoader.<MainWindow>getController().setLumine(lumine);
        stage.show();
    }
}
