package food;

import food.exception.FoodInputException;
import food.exception.FoodStorageException;


/**
 * Entry point of the program.
 *
 * <p>This class does as little as possible: it creates the {@link Ui} and the {@link Foodbot} and
 * then runs the read-and-handle loop. Keeping the loop here, rather than inside Foodbot, means
 * Foodbot never has to know where its input comes from, so it can be tested by simply calling
 * {@link Foodbot#addInput} with a String.
 */
public class Food {
    /**
     * Starts the chatbot and keeps handling commands until the user exits or input runs out.
     *
     * <p>The two exception types are treated differently on purpose: a FoodInputException is the
     * user's mistake, so it is reported and the loop carries on; a FoodStorageException means the
     * save file is unusable, so the loop stops rather than accept changes that cannot be saved.
     *
     * @param args command line arguments; not used.
     */
    public static void main(String[] args) {
        // One Ui for the whole program, shared with Foodbot, so System.in is read in one place.
        Ui ui = new Ui();

        Foodbot food;
        try {
            food = new Foodbot(ui);
        } catch (FoodStorageException e) {
            // Without a usable save file there is nothing to chat about, so report and stop.
            ui.showError(e.getMessage());
            return;
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            try {
                if (!food.addInput(input)) {
                    break;
                }
            } catch (FoodInputException e) {
                ui.showError(e.getMessage());
            } catch (FoodStorageException e) {
                ui.showLoadingError(e);
                break;
            }
        }
    }

    /** Entry point only: there is nothing to construct, so the constructor is hidden. */
    private Food() {
    }
}
