package glennon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Unicode description validation through complete command and persistence lifecycles.
 */
class UnicodeDescriptionTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_unicodeBlankDescriptions_reportsErrorsAndAllowsRecovery() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Glennon glennon = new Glennon(dataPath.toString());
        glennon.getResponse("todo keep me");
        String contents = Files.readString(dataPath);
        List<String> messages = List.of("Please enter a mission after todo.",
                "Use: deadline <mission> /by <d/M/yyyy [HHmm]>.",
                "Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.");

        for (String description : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            List<String> commands = createAddCommands(description);
            for (int i = 0; i < commands.size(); i++) {
                CommandResponse response = glennon.getCommandResponse(commands.get(i));

                assertTrue(response.isError());
                assertEquals("Mission control alert!\n" + messages.get(i), response.text());
                assertEquals("Mission log:\n1. [T][ ] keep me", glennon.getResponse("list"));
                assertEquals(contents, Files.readString(dataPath));
                assertFalse(glennon.hasExited());
            }
        }

        CommandResponse recovery = glennon.getCommandResponse("todo \u00a0recovered\u202f");

        assertFalse(recovery.isError());
        assertEquals("Mission added: [T][ ] recovered\nMission log now has 2 missions.", recovery.text());
        assertEquals("Mission log:\n1. [T][ ] keep me\n2. [T][ ] recovered",
                new Glennon(dataPath.toString()).getResponse("list"));
    }

    @Test
    void getResponse_unicodePaddedDuplicates_preservesSavedMissionsBeforeAndAfterRestart() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Glennon original = new Glennon(dataPath.toString());
        for (String command : createAddCommands("existing")) {
            assertFalse(original.getCommandResponse(command).isError());
        }
        for (int number = 1; number <= 3; number++) {
            original.getResponse("mark " + number);
        }
        String expectedList = "Mission log:\n1. [T][X] existing"
                + "\n2. [D][X] existing (by: Sep 17 2026, 9:00 AM)"
                + "\n3. [E][X] existing (from: Sep 17 2026, 9:00 AM to: Sep 17 2026, 10:00 AM)";
        String contents = Files.readString(dataPath);

        for (Glennon glennon : List.of(original, new Glennon(dataPath.toString()))) {
            for (String padding : List.of("\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
                for (String command : createAddCommands(padding + "existing" + padding)) {
                    CommandResponse response = glennon.getCommandResponse(command);

                    assertTrue(response.isError());
                    assertEquals("Mission control alert!\nThat mission already exists in the log.", response.text());
                    assertEquals(expectedList, glennon.getResponse("list"));
                    assertEquals(contents, Files.readString(dataPath));
                }
            }
        }
    }

    @Test
    void getResponse_unicodePadding_preservesInternalSpacesAndDistinctMissionsAfterRestart() {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Glennon glennon = new Glennon(dataPath.toString());
        String description = "🚀 Café\u00a0\u202f\u2007  reading\t@図書館 😊";
        for (String command : createAddCommands("\u00a0\u202f\u2007" + description + "\u2007\u202f\u00a0")) {
            assertFalse(glennon.getCommandResponse(command).isError());
        }
        for (String command : createAddCommands("🚀 Café reading @図書館 😊")) {
            assertFalse(glennon.getCommandResponse(command).isError());
        }
        String expectedList = "Mission log:\n1. [T][ ] " + description
                + "\n2. [D][ ] " + description + " (by: Sep 17 2026, 9:00 AM)"
                + "\n3. [E][ ] " + description + " (from: Sep 17 2026, 9:00 AM to: Sep 17 2026, 10:00 AM)"
                + "\n4. [T][ ] 🚀 Café reading @図書館 😊"
                + "\n5. [D][ ] 🚀 Café reading @図書館 😊 (by: Sep 17 2026, 9:00 AM)"
                + "\n6. [E][ ] 🚀 Café reading @図書館 😊 (from: Sep 17 2026, 9:00 AM to: Sep 17 2026, 10:00 AM)";

        assertEquals(expectedList, glennon.getResponse("list"));
        assertEquals(expectedList, new Glennon(dataPath.toString()).getResponse("list"));
    }

    @Test
    void getWelcome_unicodeBlankStoredDescriptions_blocksChangesWithoutOverwriting() throws IOException {
        for (String description : List.of("\u00a0", "\u202f", "\u2007")) {
            for (String record : createStoredRecords(description)) {
                assertStartupFailurePreservesData(record + "\n", "Mission data is corrupted at line 1.");
            }
        }
    }

    @Test
    void getWelcome_unicodePaddedStoredDuplicates_blocksChangesWithoutOverwriting() throws IOException {
        List<String> originalRecords = createStoredRecords("existing");
        for (String padding : List.of("\u00a0", "\u202f", "\u2007")) {
            List<String> paddedRecords = createStoredRecords(padding + "existing" + padding);
            for (int i = 0; i < originalRecords.size(); i++) {
                String contents = originalRecords.get(i) + "\n"
                        + paddedRecords.get(i).replace("\t0\t", "\t1\t") + "\n";

                assertStartupFailurePreservesData(contents, "Mission data is corrupted at line 2: duplicate mission.");
            }
        }
    }

    /** Returns an add command for each supported mission type with the same description. */
    private static List<String> createAddCommands(String description) {
        return List.of("todo " + description, "deadline " + description + " /by 17/9/2026 0900",
                "event " + description + " /from 17/9/2026 0900 /to 17/9/2026 1000");
    }

    /** Returns stored records independently of the production serializer. */
    private static List<String> createStoredRecords(String description) {
        String encodedDescription = encode(description);
        String start = encode("2026-09-17T09:00");
        String end = encode("2026-09-17T10:00");
        return List.of("T\t0\t" + encodedDescription, "D\t0\t" + encodedDescription + "\t" + start,
                "E\t0\t" + encodedDescription + "\t" + start + "\t" + end);
    }

    /** Verifies that rejected stored descriptions block writes in every new session. */
    private void assertStartupFailurePreservesData(String contents, String message) throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Files.writeString(dataPath, contents);
        for (int attempt = 0; attempt < 2; attempt++) {
            Glennon glennon = new Glennon(dataPath.toString());
            String expectedError = "Mission control alert!\n" + message;

            assertEquals(expectedError, glennon.getWelcome());
            assertTrue(glennon.hasStartupError());
            assertEquals(expectedError, glennon.getResponse("todo cannot replace data"));
            assertEquals(expectedError, glennon.getResponse("delete 1"));
            assertEquals(contents, Files.readString(dataPath));
        }
    }

    /** Encodes fixture values as UTF-8 Base64 fields. */
    private static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
}
