package kaykay.command;

import java.io.IOException;

import kaykay.model.ApplicationData;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Displays all saved places in their current order.
 */
public final class ListPlacesCommand extends Command {
    /** Creates a command that lists places. */
    public ListPlacesCommand() {
    }

    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage) throws IOException {
        ui.showPlaceList(data.getPlaces());
    }
}
