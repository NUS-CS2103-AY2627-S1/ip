import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import sunny.Sunny;

/**
 * A GUI for Sunny using FXML.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Sunny sunny = new Sunny();
            stage.setTitle(sunny.getTitle());
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/sunny.png")));
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setSunny(sunny);  // inject Sunny
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) { //if any class is missing
            showFatalError();
        }
    }

    /**
     * Creates a small window telling the user that the app is broken.
     */
    private static void showFatalError() {
        javax.swing.JOptionPane.showMessageDialog(
                null,
                "Hey, genius! I could not start because a required component is missing!\n"
                        + "Please reinstall the application. Or don't. It's not like I care, or anything.",
                "Sunny - Fatal Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
        );
    }
}
