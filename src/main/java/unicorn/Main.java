package unicorn;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import unicorn.ui.MainWindow;

/**
 * Displays the Unicorn graphical user interface using FXML.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 480;
    private static final double MINIMUM_WINDOW_WIDTH = 380;

    private final Unicorn unicorn = new Unicorn();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setUnicorn(unicorn);

        stage.setScene(new Scene(mainLayout));
        stage.setTitle("Prisma — Wise Tech Unicorn");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Unicorn.png")));
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.show();
    }
}
