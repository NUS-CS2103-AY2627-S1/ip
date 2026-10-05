package kaykay.command;

import java.io.IOException;

import kaykay.model.ApplicationData;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Displays places whose recorded details contain a keyword.
 */
public final class FindPlacesCommand extends Command {
    /** Keyword to search for in place details. */
    private final String keyword;

    /**
     * Creates a place search command.
     *
     * @param keyword text to search for.
     */
    public FindPlacesCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage) throws IOException {
        ui.showMatchingPlaces(data.getPlaces().findPlaces(keyword));
    }
}
