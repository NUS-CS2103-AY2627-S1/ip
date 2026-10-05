package kaykay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kaykay.model.Place;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/** Tests one-command processing independently of the console input loop. */
class KaykayTest {
    @TempDir
    Path temporaryDirectory;

    /** Checks that commands execute and render their responses through the UI. */
    @Test
    void shouldExitAfterProcessingCommand_validCommand_updatesTasksAndRendersResponse() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(line -> output.append(line).append(System.lineSeparator()));
        Kaykay kaykay = new Kaykay(temporaryDirectory.resolve("tasks.txt").toString(), ui);

        assertFalse(kaykay.shouldExitAfterProcessingCommand("todo buy milk"));
        assertTrue(output.toString().contains("Mission added:"));
        assertTrue(output.toString().contains("[T][ ] buy milk"));
        assertTrue(kaykay.shouldExitAfterProcessingCommand("bye"));
    }

    /** Checks that GUI output omits separators while retaining response text. */
    @Test
    void shouldExitAfterProcessingCommand_guiOutput_omitsConsoleSeparators() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(line -> output.append(line).append(System.lineSeparator()), false);
        Kaykay kaykay = new Kaykay(temporaryDirectory.resolve("gui-tasks.txt").toString(), ui);

        kaykay.shouldExitAfterProcessingCommand("todo buy milk");

        assertFalse(output.toString().contains("____________________________________________________________"));
        assertTrue(output.toString().contains("Mission added:"));
    }

    /** Checks that a blank command produces concise and predictable guidance. */
    @Test
    void shouldExitAfterProcessingCommand_blankCommand_showsInputWarning() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(line -> output.append(line).append(System.lineSeparator()), false);
        Kaykay kaykay = new Kaykay(temporaryDirectory.resolve("blank-command.txt").toString(), ui);

        assertFalse(kaykay.shouldExitAfterProcessingCommand("   "));

        assertEquals("Mission snag: Please enter a command." + System.lineSeparator(), output.toString());
    }

    /** Checks that GUI errors can be styled separately without changing their wording. */
    @Test
    void shouldExitAfterProcessingCommand_guiError_routesToErrorOutput() {
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        Ui ui = new Ui(
                line -> output.append(line).append(System.lineSeparator()),
                line -> errorOutput.append(line).append(System.lineSeparator()),
                false);
        Kaykay kaykay = new Kaykay(temporaryDirectory.resolve("gui-error.txt").toString(), ui);

        assertFalse(kaykay.shouldExitAfterProcessingCommand("unknown"));

        assertEquals("", output.toString());
        assertEquals("Mission snag: I don't recognise that command. Try todo, deadline, event, list, find, delete, "
                + "mark, unmark, place, or bye." + System.lineSeparator(), errorOutput.toString());
    }

    /** Checks that GUI action confirmations can be styled separately without changing their wording. */
    @Test
    void shouldExitAfterProcessingCommand_guiSuccess_routesToSuccessOutput() {
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        StringBuilder successOutput = new StringBuilder();
        Ui ui = new Ui(
                line -> output.append(line).append(System.lineSeparator()),
                line -> errorOutput.append(line).append(System.lineSeparator()),
                line -> successOutput.append(line).append(System.lineSeparator()),
                false);
        Kaykay kaykay = new Kaykay(temporaryDirectory.resolve("gui-success.txt").toString(), ui);

        assertFalse(kaykay.shouldExitAfterProcessingCommand("todo buy milk"));

        assertEquals("", output.toString());
        assertEquals("", errorOutput.toString());
        assertEquals("Mission added:" + System.lineSeparator()
                + "[T][ ] buy milk" + System.lineSeparator()
                + "Now you have 1 task in the list." + System.lineSeparator(), successOutput.toString());
    }

    /** Checks that place commands work together through the application coordinator. */
    @Test
    void shouldExitAfterProcessingCommand_placeWorkflow_managesPersistedPlaces() throws IOException {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(line -> output.append(line).append(System.lineSeparator()), false);
        Path dataFile = temporaryDirectory.resolve("place-data.txt");
        Kaykay kaykay = new Kaykay(dataFile.toString(), ui);

        kaykay.shouldExitAfterProcessingCommand("place add Burnt Ends /type restaurant /location Dempsey "
                + "/visited 10 09 2026 /rating 5 /notes Great brisket");
        kaykay.shouldExitAfterProcessingCommand("place edit 1 /rating 4 /notes Worth revisiting");
        kaykay.shouldExitAfterProcessingCommand("place find REVISITING");
        kaykay.shouldExitAfterProcessingCommand("place list");

        assertTrue(output.toString().contains("Location logged:"));
        assertTrue(output.toString().contains("rating: 4/5; notes: Worth revisiting"));
        assertTrue(output.toString().contains("Here are the matching locations:"));
        assertEquals(1, new Storage(dataFile.toString()).loadData().getPlaces().size());
    }

    /**
     * Checks field extraction and ensures malformed place commands leave saved data intact.
     */
    @Test
    void placeCommands_missingFields_preserveExistingDetails() throws IOException {
        Path dataFile = temporaryDirectory.resolve("place-validation.txt");
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(line -> output.append(line).append(System.lineSeparator()), false);
        Kaykay kaykay = new Kaykay(dataFile.toString(), ui);
        kaykay.shouldExitAfterProcessingCommand("place add A/B Cafe /notes https://example.com/menu "
                + "/rating 5 /type coffee shop");
        Place savedPlace = new Storage(dataFile.toString()).loadData().getPlaces().getPlace(0);
        assertEquals("A/B Cafe", savedPlace.getName());
        assertEquals("https://example.com/menu", savedPlace.getNotes());
        assertEquals(5, savedPlace.getRating());
        assertEquals("coffee shop", savedPlace.getType());
        String originalContents = Files.readString(dataFile);

        kaykay.shouldExitAfterProcessingCommand("place add /rating 5");
        kaykay.shouldExitAfterProcessingCommand("place add Cafe /type /rating 5");
        kaykay.shouldExitAfterProcessingCommand("place edit 1 /type /rating 4");

        assertEquals(originalContents, Files.readString(dataFile));
        output.setLength(0);
        kaykay.shouldExitAfterProcessingCommand("place list");
        assertEquals("Here are your logged locations:" + System.lineSeparator()
                + "1. [P] A/B Cafe (type: coffee shop; rating: 5/5; notes: https://example.com/menu)"
                + System.lineSeparator(), output.toString());
    }

    /** Checks that malformed startup data is reported and protected from later writes. */
    @Test
    void startup_invalidData_reportsErrorAndPreventsOverwrite() throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid-data.txt");
        String invalidData = "Q | 2 | broken task" + System.lineSeparator();
        Files.writeString(dataFile, invalidData);
        StringBuilder errorOutput = new StringBuilder();
        Ui ui = new Ui(line -> { },
                line -> errorOutput.append(line).append(System.lineSeparator()), false);
        Kaykay kaykay = new Kaykay(dataFile.toString(), ui);

        kaykay.showInitialDataLoadError();
        kaykay.shouldExitAfterProcessingCommand("todo do not overwrite");

        assertTrue(errorOutput.toString().contains("I couldn't load your data"));
        assertTrue(errorOutput.toString().contains("I couldn't save your data"));
        assertEquals(invalidData, Files.readString(dataFile));
    }
}
