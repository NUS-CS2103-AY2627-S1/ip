package kojispawn;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import kojispawn.exception.KojisPawnException;
import kojispawn.ui.MainWindow;

/**
 * Displays the JavaFX interface for Koji's Pawn.
 */
public class Main extends Application {
    private KojisPawn koji;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            koji = new KojisPawn();
        } catch (KojisPawnException exception) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Koji's Pawn");
            errorAlert.setHeaderText("Unable to load task data");
            errorAlert.setContentText(exception.getMessage());
            errorAlert.showAndWait();
            Platform.exit();
            return;
        }

        Font.loadFont(Main.class.getResourceAsStream("/fonts/Cinzel.ttf"), 20.0);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);

        stage.setTitle("Koji's Pawn");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/PawnIcon.png")));
        stage.setMinHeight(220.0);
        stage.setMinWidth(417.0);
        stage.setScene(scene);
        fxmlLoader.<MainWindow>getController().setKoji(koji);
        stage.show();
    }
}
