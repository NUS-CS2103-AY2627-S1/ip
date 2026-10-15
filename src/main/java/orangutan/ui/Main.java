package orangutan.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import orangutan.Orangutan;

/**
 * A GUI for Orangutan using FXML.
 * All FXML markup, stylesheets, and java code are adapted from se-education.org/guides/tutorials/javaFx.html.
 */
public class Main extends Application {
    private static final String APPLICATION_NAME = "Orangutan";
    private static final String DATA_PATH = "./data/orangutan.txt";
    private static final String ICON_PATH = "/images/icon.png";
    private static final int WINDOW_MIN_HEIGHT = 220;
    private static final int WINDOW_MIN_WIDTH = 417;

    private final Orangutan orangutan = new Orangutan(DATA_PATH, true);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setMinHeight(WINDOW_MIN_HEIGHT);
            stage.setMinWidth(WINDOW_MIN_WIDTH);

            // set taskbar icon and title
            // icon taken from www.flaticon.com/free-icon/orangutan_7743169
            Image icon = new Image(this.getClass().getResourceAsStream(ICON_PATH));
            stage.getIcons().add(icon);
            stage.setTitle(APPLICATION_NAME);

            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setOrangutan(orangutan); // inject Orangutan instance
            fxmlLoader.<MainWindow>getController().welcome(); // display welcome message
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
