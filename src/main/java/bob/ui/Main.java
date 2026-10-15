package bob.ui;

import java.awt.Taskbar;
import java.io.IOException;

import javax.imageio.ImageIO;

import bob.Bob;
import bob.storage.Storage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Runs the chatbot as a windowed program.
 *
 * <p>This is the graphical counterpart of {@link Bob#main}: it makes a chatbot,
 * gives it something to talk through, and steps aside. The difference is that a
 * console program then runs a loop until the input ends, while this one hands its
 * window to JavaFX and returns — from there on the user's typing and clicking are
 * what make anything happen, and {@link MainWindow} answers them.
 *
 * <p>Extending {@link Application} is what makes this class a JavaFX program:
 * {@link Application#launch} sets up the toolkit, opens a window, and calls
 * {@link #start} on the thread that JavaFX draws on. That launch is done from
 * {@link bob.Launcher Launcher} rather than from a {@code main} here, for the reason given there.
 *
 * <p>The chatbot is made here, and only here, so that the window below is left
 * knowing how to show a conversation without also having to decide whose it is or
 * where its tasks are kept. It reads and writes the same save file as the console
 * chatbot, so a task added in one is there in the other.
 */
public class Main extends Application {

    /**
     * Narrowest the window may be dragged to: enough for the Send button and a
     * useful amount of the text field beside it.
     */
    private static final double MIN_WIDTH = 380.0;

    /** Shortest the window may be dragged to: the input row and a bubble or two. */
    private static final double MIN_HEIGHT = 300.0;

    /** The chatbot the window talks to, kept in the same save file the console one uses. */
    private final Bob bob = Bob.forGui(Storage.DEFAULT_FILE_PATH);

    /**
     * Builds the window from {@code /view/MainWindow.fxml}, points it at the
     * chatbot, and shows it.
     *
     * <p>The controller is fetched from the loader rather than made here, because
     * the loader has already made one — the FXML names the class — and it is that
     * one the window's controls were given to. Handing the chatbot to any other
     * would leave the window talking to nobody.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setBob(bob);

            stage.setScene(new Scene(root));
            // The close button would otherwise close the window without asking the
            // chatbot, losing any change it has not yet managed to save.
            stage.setOnCloseRequest(mainWindow::handleCloseRequest);
            stage.setTitle("King Bob");
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);
            stage.getIcons().add(Images.BOB);
            showBobInTheDock();
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged with the program, so failing to read it means a
            // broken build rather than anything the user could put right.
            throw new IllegalStateException("Could not load the chatbot's window layout", e);
        }
    }

    /**
     * Puts the chatbot's picture on the program's icon in the dock or task bar,
     * where the operating system will take one.
     *
     * <p>{@link Stage#getIcons()} above is enough on Windows and on most Linux
     * desktops, where the window carries its own icon and the task bar shows it.
     * macOS works the other way round: its windows have no icon at all, and the one
     * in the dock belongs to the program rather than to any window, so it is set
     * here instead. Without this the chatbot sits in the dock under the generic
     * coffee cup, looking like a stray copy of Java rather than like Bob.
     *
     * <p>{@link Taskbar} is one of the older AWT classes rather than a JavaFX one,
     * because JavaFX has nothing for this; that is also why the picture is read
     * again through {@link ImageIO} rather than reusing {@link Images#BOB}, which is
     * a JavaFX image and not a kind AWT understands.
     *
     * <p>A desktop that does not offer this is not worth failing over: it simply
     * keeps the icon it would have had, which costs the user nothing. Only the two
     * ways that can happen are caught — the platform declining, and the picture not
     * reading — so a mistake in this code still shows up instead of being taken for
     * an unusual desktop.
     */
    private static void showBobInTheDock() {
        try {
            if (!Taskbar.isTaskbarSupported()) {
                return;
            }
            Taskbar taskbar = Taskbar.getTaskbar();
            if (!taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                return;
            }
            taskbar.setIconImage(ImageIO.read(Main.class.getResource(Images.BOB_PATH)));
        } catch (IOException | UnsupportedOperationException e) {
            // A desktop with no dock icon to set (a headless one included, since
            // HeadlessException is an UnsupportedOperationException), or a picture
            // that could not be read. The default icon will do.
        }
    }
}
