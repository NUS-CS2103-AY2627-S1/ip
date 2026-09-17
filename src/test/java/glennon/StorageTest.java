package glennon;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import glennon.exception.GlennonException;
import glennon.task.Deadline;
import glennon.task.Event;
import glennon.task.Task;
import glennon.task.Todo;

/**
 * Tests mission persistence without touching the application's real data file.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadMissions_missingFile_returnsEmptyList() throws GlennonException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadMissions().isEmpty());
    }

    @Test
    void loadMissions_missingParent_returnsEmptyList() throws GlennonException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing").resolve("missions.txt"));

        assertTrue(storage.loadMissions().isEmpty());
    }

    @Test
    void loadMissions_missingAncestors_returnsEmptyListWithoutCreatingFolders() throws GlennonException {
        Path missingParent = temporaryDirectory.resolve("missing");
        Storage storage = new Storage(missingParent.resolve("nested").resolve("missions.txt"));

        assertTrue(storage.loadMissions().isEmpty());
        assertFalse(Files.exists(missingParent));
    }

    @Test
    void loadMissions_emptyFile_returnsMutableEmptyList() throws IOException, GlennonException {
        Path dataPath = Files.createFile(temporaryDirectory.resolve("missions.txt"));

        List<Task> missions = new Storage(dataPath).loadMissions();

        assertTrue(missions.isEmpty());
        missions.add(new Todo("new mission"));
        assertEquals(1, missions.size());
        assertEquals(0, Files.size(dataPath));
    }

    @Test
    void loadMissions_windowsLineEndingsWithoutFinalNewline_preservesRecords()
            throws IOException, GlennonException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        String contents = "T\t1\t" + encode("first mission") + "\r\nT\t0\t" + encode("second mission");
        Files.writeString(dataPath, contents);

        List<Task> missions = new Storage(dataPath).loadMissions();

        assertEquals(2, missions.size());
        assertEquals("first mission", missions.getFirst().getDescription());
        assertTrue(missions.getFirst().isDone());
        assertEquals("second mission", missions.getLast().getDescription());
        assertFalse(missions.getLast().isDone());
        assertEquals(contents, Files.readString(dataPath));
    }

    @Test
    void loadMissions_directoryPath_reportsLoadFailure() {
        GlennonException exception = assertThrows(
                GlennonException.class, () -> new Storage(temporaryDirectory).loadMissions());

        assertEquals("Glennon could not load the mission data.", exception.getMessage());
        assertTrue(Files.isDirectory(temporaryDirectory));
    }

    @Test
    void loadMissions_parentIsFile_reportsLoadFailure() throws IOException {
        Path parent = temporaryDirectory.resolve("parent.txt");
        Files.writeString(parent, "keep parent contents");

        GlennonException exception = assertThrows(
                GlennonException.class, () -> new Storage(parent.resolve("missions.txt")).loadMissions());

        assertEquals("Glennon could not load the mission data.", exception.getMessage());
        assertEquals("keep parent contents", Files.readString(parent));
    }

    @Test
    void loadMissions_ancestorIsFile_reportsLoadFailureAndPreservesContents() throws IOException {
        Path ancestor = temporaryDirectory.resolve("ancestor.txt");
        Files.writeString(ancestor, "keep ancestor contents");
        Path dataPath = ancestor.resolve("missing").resolve("nested").resolve("missions.txt");

        GlennonException exception = assertThrows(
                GlennonException.class, () -> new Storage(dataPath).loadMissions());

        assertEquals("Glennon could not load the mission data.", exception.getMessage());
        assertEquals("keep ancestor contents", Files.readString(ancestor));
    }

    @Test
    void loadMissions_readDenied_reportsPermissionsWithoutDiscardingData() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Files.writeString(dataPath, "T\t0\tdGFzaw==\n");
        assumeTrue(Files.getFileStore(dataPath).supportsFileAttributeView("posix"));
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(dataPath);
        GlennonException exception;
        try {
            Files.setPosixFilePermissions(dataPath, Set.of());
            assumeFalse(Files.isReadable(dataPath), "This account can bypass read permissions.");
            exception = assertThrows(GlennonException.class, () -> new Storage(dataPath).loadMissions());
        } finally {
            Files.setPosixFilePermissions(dataPath, permissions);
        }

        assertEquals("Glennon cannot read the mission data. Check the file and folder permissions.",
                exception.getMessage());
        assertEquals("T\t0\tdGFzaw==\n", Files.readString(dataPath));
    }

    @Test
    void saveAndLoadMissions_allTaskTypes_preservesFieldsAndStatus()
            throws GlennonException {
        Path dataPath = temporaryDirectory.resolve("nested").resolve("missions.txt");
        Storage storage = new Storage(dataPath);
        Todo todo = new Todo("tabs\t and Unicode 新加坡");
        Deadline deadline = new Deadline(
                "submit report", LocalDateTime.of(2026, 8, 31, 23, 59));
        Event event = new Event(
                "overnight event",
                LocalDateTime.of(2026, 9, 1, 22, 0),
                LocalDateTime.of(2026, 9, 2, 1, 30));
        deadline.markAsDone();

        storage.saveMissions(List.of(todo, deadline, event));
        List<Task> loaded = storage.loadMissions();

        assertEquals(3, loaded.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loaded.get(0));
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loaded.get(1));
        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(2));
        assertEquals(todo.getDescription(), loadedTodo.getDescription());
        assertFalse(loadedTodo.isDone());
        assertEquals(deadline.getDescription(), loadedDeadline.getDescription());
        assertEquals(deadline.getDueDateTime(), loadedDeadline.getDueDateTime());
        assertTrue(loadedDeadline.isDone());
        assertEquals(event.getDescription(), loadedEvent.getDescription());
        assertEquals(event.getStartDateTime(), loadedEvent.getStartDateTime());
        assertEquals(event.getEndDateTime(), loadedEvent.getEndDateTime());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    void saveMissions_allTaskTypes_writesExpectedPortableRecords() throws IOException, GlennonException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Todo todo = new Todo("带伞\t☂️");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2026, 9, 17, 9, 0));
        Event event = new Event("meet", LocalDateTime.of(2026, 9, 17, 10, 0),
                LocalDateTime.of(2026, 9, 17, 11, 0));
        event.markAsDone();

        new Storage(dataPath).saveMissions(List.of(todo, deadline, event));

        assertEquals(List.of("T\t1\t" + encode("带伞\t☂️"),
                "D\t0\t" + encode("submit") + "\t" + encode("2026-09-17T09:00"),
                "E\t1\t" + encode("meet") + "\t" + encode("2026-09-17T10:00")
                        + "\t" + encode("2026-09-17T11:00")), Files.readAllLines(dataPath));
        assertTrue(Files.readString(dataPath).chars().allMatch(character -> character < 128));
        assertOnlyDataFileRemains(dataPath);
    }

    @Test
    void saveAndLoadMissions_preciseTimesAndSharedDescriptions_preservesDistinctMissions()
            throws GlennonException {
        Storage storage = new Storage(temporaryDirectory.resolve("missions.txt"));
        LocalDateTime firstDateTime = LocalDateTime.of(2026, 9, 17, 9, 0, 12, 123456789);
        LocalDateTime secondDateTime = firstDateTime.plusNanos(1);
        LocalDateTime thirdDateTime = secondDateTime.plusNanos(1);
        Todo todo = new Todo("shared description 🚀");
        Deadline firstDeadline = new Deadline(todo.getDescription(), firstDateTime);
        Deadline secondDeadline = new Deadline(todo.getDescription(), secondDateTime);
        Event firstEvent = new Event(todo.getDescription(), firstDateTime, secondDateTime);
        Event secondEvent = new Event(todo.getDescription(), firstDateTime, thirdDateTime);
        Event thirdEvent = new Event(todo.getDescription(), secondDateTime, thirdDateTime);
        List<Task> originalMissions = List.of(todo, firstDeadline, secondDeadline,
                firstEvent, secondEvent, thirdEvent);
        originalMissions.forEach(Task::markAsDone);

        storage.saveMissions(originalMissions);
        List<Task> loadedMissions = storage.loadMissions();

        assertEquals(originalMissions.size(), loadedMissions.size());
        for (int i = 0; i < originalMissions.size(); i++) {
            assertTrue(originalMissions.get(i).hasSameDetails(loadedMissions.get(i)));
            assertTrue(loadedMissions.get(i).isDone());
        }
    }

    @Test
    void loadMissions_modifiedResult_doesNotAffectLaterLoads() throws GlennonException {
        Storage storage = new Storage(temporaryDirectory.resolve("missions.txt"));
        storage.saveMissions(List.of(new Todo("saved mission")));

        List<Task> firstLoad = storage.loadMissions();
        firstLoad.getFirst().markAsDone();
        firstLoad.add(new Todo("unsaved mission"));
        List<Task> secondLoad = storage.loadMissions();

        assertEquals(1, secondLoad.size());
        assertEquals("saved mission", secondLoad.getFirst().getDescription());
        assertFalse(secondLoad.getFirst().isDone());
    }

    @Test
    void saveMissions_repeatedReplacement_persistsLatestOrderAndStatus()
            throws IOException, GlennonException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Storage storage = new Storage(dataPath);
        Todo firstMission = new Todo("first mission");
        Todo secondMission = new Todo("second mission");
        storage.saveMissions(List.of(firstMission, secondMission));
        secondMission.markAsDone();

        storage.saveMissions(List.of(secondMission, firstMission));

        List<Task> missions = storage.loadMissions();
        assertEquals(List.of("second mission", "first mission"),
                missions.stream().map(Task::getDescription).toList());
        assertTrue(missions.getFirst().isDone());
        assertFalse(missions.getLast().isDone());
        assertOnlyDataFileRemains(dataPath);
    }

    @Test
    void saveMissions_emptyList_replacesExistingData() throws IOException, GlennonException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Storage storage = new Storage(dataPath);
        storage.saveMissions(List.of(new Todo("temporary")));

        storage.saveMissions(List.of());

        assertTrue(storage.loadMissions().isEmpty());
        assertTrue(Files.exists(dataPath));
        assertOnlyDataFileRemains(dataPath);
    }

    @Test
    void loadMissions_corruptedSecondLine_reportsLineNumber() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Files.writeString(dataPath, "T\t0\tdmFsaWQ=\ninvalid\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataPath);

        GlennonException exception = assertThrows(
                GlennonException.class, storage::loadMissions);

        assertEquals("Mission data is corrupted at line 2.", exception.getMessage());
    }

    @Test
    void loadMissions_invalidStatus_reportsCorruption() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Files.writeString(dataPath, "T\t2\tdGFzaw==\n", StandardCharsets.UTF_8);

        GlennonException exception = assertThrows(
                GlennonException.class, () -> new Storage(dataPath).loadMissions());

        assertEquals("Mission data is corrupted at line 1.", exception.getMessage());
    }

    @Test
    void loadMissions_invalidDateFields_reportsCorruptedLine() throws IOException {
        String validDate = encode("2026-09-17T09:00");
        String invalidDate = encode("2026-02-30T09:00");
        for (String line : List.of(
                "D\t0\tdGFzaw==\t" + invalidDate,
                "E\t0\tdGFzaw==\t" + invalidDate + "\t" + validDate,
                "E\t0\tdGFzaw==\t" + validDate + "\t" + invalidDate,
                "D\t0\tdGFzaw==\t" + encode("not a date"))) {
            assertCorruptedSecondLine(line);
        }
    }

    @Test
    void loadMissions_blankDescriptions_reportsCorruptedLine() throws IOException {
        String start = encode("2026-09-17T09:00");
        String end = encode("2026-09-17T10:00");
        for (String description : List.of("", " ", "\t\n", "\u2003", "\u00a0", "\u202f", "\u2007",
                " \t\u00a0\u202f\u2007\u2003 ")) {
            assertCorruptedSecondLine("T\t0\t" + encode(description));
            assertCorruptedSecondLine("D\t0\t" + encode(description) + "\t" + start);
            assertCorruptedSecondLine("E\t0\t" + encode(description) + "\t" + start + "\t" + end);
        }
    }

    @Test
    void loadMissions_reversedEvent_reportsCorruptedLine() throws IOException {
        assertCorruptedSecondLine("E\t0\tdGFzaw==\t" + encode("2026-09-18T09:00")
                + "\t" + encode("2026-09-17T09:00"));
    }

    @Test
    void loadMissions_equalEventTimes_reportsCorruptedLine() throws IOException {
        String dateTime = encode("2026-09-17T09:00");
        assertCorruptedSecondLine("E\t0\tdGFzaw==\t" + dateTime + "\t" + dateTime);
    }

    @Test
    void loadMissions_controlCharactersInDescription_reportsCorruptedLine() throws IOException {
        for (String description : List.of("line\nbreak", "hidden\u0000control", "unicode\u2028break")) {
            assertCorruptedSecondLine("T\t0\t" + encode(description));
        }
    }

    @Test
    void loadMissions_duplicateTaskTypesAndStatuses_reportsLineAndPreservesFile() throws IOException {
        String dateTime = encode("2026-09-17T09:00");
        String endDateTime = encode("2026-09-17T10:00");
        List<String> records = List.of("T\t0\tdGFzaw==", "D\t0\tdGFzaw==\t" + dateTime,
                "E\t0\tdGFzaw==\t" + dateTime + "\t" + endDateTime);
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        for (String record : records) {
            String contents = record + "\n" + record.replace("\t0\t", "\t1\t") + "\n";
            Files.writeString(dataPath, contents);

            GlennonException exception = assertThrows(
                    GlennonException.class, () -> new Storage(dataPath).loadMissions());

            assertEquals("Mission data is corrupted at line 2: duplicate mission.", exception.getMessage());
            assertEquals(contents, Files.readString(dataPath));
        }
    }

    @Test
    void loadMissions_invalidBase64_reportsCorruptedLine() throws IOException {
        assertCorruptedSecondLine("T\t0\t%%%");
        assertCorruptedSecondLine("D\t0\tdGFzaw==\t%%%");
        assertCorruptedSecondLine("E\t0\tdGFzaw==\t%%%\t" + encode("2026-09-17T10:00"));
        assertCorruptedSecondLine("E\t0\tdGFzaw==\t" + encode("2026-09-17T09:00") + "\t%%%");
    }

    @Test
    void loadMissions_invalidDecodedUtf8_reportsCorruptedLine() throws IOException {
        assertCorruptedSecondLine("T\t0\t/w==");
        assertCorruptedSecondLine("T\t0\tww==");
        assertCorruptedSecondLine("D\t0\tdGFzaw==\t/w==");
    }

    @Test
    void loadMissions_invalidRawUtf8_reportsCorruptedLine() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        byte[] corruptedData = { 'T', '\t', '0', '\t', (byte) 0xff, '\n' };
        Files.write(dataPath, corruptedData);

        GlennonException exception = assertThrows(
                GlennonException.class, () -> new Storage(dataPath).loadMissions());

        assertEquals("Mission data is corrupted at line 1.", exception.getMessage());
        assertArrayEquals(corruptedData, Files.readAllBytes(dataPath));
    }

    @Test
    void loadMissions_wrongFieldCount_reportsCorruptedLine() throws IOException {
        assertCorruptedSecondLine("T\t0");
        assertCorruptedSecondLine("D\t0\tdGFzaw==");
        assertCorruptedSecondLine("E\t0\tdGFzaw==\t" + encode("2026-09-17T09:00"));
        assertCorruptedSecondLine("T\t0\tdGFzaw==\textra");
        assertCorruptedSecondLine("T\t0\tdGFzaw==\t");
        assertCorruptedSecondLine("D\t0\tdGFzaw==\t" + encode("2026-09-17T09:00") + "\textra");
        assertCorruptedSecondLine("E\t0\tdGFzaw==\t" + encode("2026-09-17T09:00")
                + "\t" + encode("2026-09-17T10:00") + "\textra");
    }

    @Test
    void loadMissions_unknownTypeAndBlankRecords_reportsCorruptedLine() throws IOException {
        for (String record : List.of("", " ", "\t\t", "X\t0\tdGFzaw==", "t\t0\tdGFzaw==")) {
            assertCorruptedSecondLine(record);
        }
    }

    @Test
    void loadMissions_descriptionWhitespaceCreatesDuplicate_reportsCorruptedLine() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        String start = encode("2026-09-17T09:00");
        String end = encode("2026-09-17T10:00");
        List<String> records = List.of("T\t0\t" + encode("task"),
                "D\t0\t" + encode("task") + "\t" + start,
                "E\t0\t" + encode("task") + "\t" + start + "\t" + end);
        for (String padding : List.of("\t \u2003", "\u00a0", "\u202f", "\u2007", " \t\u00a0\u202f\u2007 ")) {
            for (String record : records) {
                String paddedRecord = record.replace("\t0\t", "\t1\t")
                        .replace(encode("task"), encode(padding + "task" + padding));
                String contents = record + "\n" + paddedRecord + "\n";
                Files.writeString(dataPath, contents);

                GlennonException exception = assertThrows(
                        GlennonException.class, () -> new Storage(dataPath).loadMissions());

                assertEquals("Mission data is corrupted at line 2: duplicate mission.", exception.getMessage());
                assertEquals(contents, Files.readString(dataPath));
            }
        }
    }

    @Test
    void loadAndSaveMissions_unicodeSpacePadding_preservesInternalTextAndTaskDetails()
            throws IOException, GlennonException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        String description = "🚀 Café\u00a0\u202f\u2007  reading\t@図書館 😊";
        String storedDescription = encode(" \t\u00a0\u202f\u2007" + description + "\u2007\u202f\u00a0\t ");
        String start = encode("2026-09-17T09:00");
        String end = encode("2026-09-17T10:00");
        String contents = "T\t1\t" + storedDescription + "\nD\t1\t" + storedDescription + "\t" + start
                + "\nE\t1\t" + storedDescription + "\t" + start + "\t" + end + "\n";
        Files.writeString(dataPath, contents);
        Storage storage = new Storage(dataPath);

        List<Task> loaded = storage.loadMissions();

        assertEquals(3, loaded.size());
        for (Task task : loaded) {
            assertEquals(description, task.getDescription());
            assertTrue(task.isDone());
        }
        assertEquals(contents, Files.readString(dataPath));

        storage.saveMissions(loaded);

        List<Task> reloaded = new Storage(dataPath).loadMissions();
        assertEquals(loaded.size(), reloaded.size());
        for (int i = 0; i < loaded.size(); i++) {
            assertTrue(loaded.get(i).hasSameDetails(reloaded.get(i)));
            assertTrue(reloaded.get(i).isDone());
        }
        assertOnlyDataFileRemains(dataPath);
    }

    @Test
    void saveMissions_unsupportedTaskType_preservesExistingFile() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        String originalData = "T\t0\tdGFzaw==\n";
        Files.writeString(dataPath, originalData);
        Storage storage = new Storage(dataPath);
        Task unsupportedTask = new Task("unsupported") { };

        GlennonException exception = assertThrows(
                GlennonException.class, () -> storage.saveMissions(List.of(unsupportedTask)));

        assertEquals("Glennon cannot save an unsupported mission type.", exception.getMessage());
        assertEquals(originalData, Files.readString(dataPath));
        assertOnlyDataFileRemains(dataPath);
    }

    @Test
    void saveMissions_unsupportedTaskAfterValidTasks_doesNotCreateDirectories() {
        Path parentPath = temporaryDirectory.resolve("missing");
        Storage storage = new Storage(parentPath.resolve("missions.txt"));
        Task unsupportedTask = new Task("unsupported") { };
        List<Task> missions = List.of(new Todo("valid mission"), unsupportedTask);

        GlennonException exception = assertThrows(
                GlennonException.class, () -> storage.saveMissions(missions));

        assertEquals("Glennon cannot save an unsupported mission type.", exception.getMessage());
        assertFalse(Files.exists(parentPath));
    }

    @Test
    void saveMissions_parentIsFile_preservesParentContents() throws IOException {
        Path parentPath = temporaryDirectory.resolve("parent.txt");
        Files.writeString(parentPath, "keep parent contents");

        GlennonException exception = assertThrows(GlennonException.class, () -> new Storage(
                parentPath.resolve("missions.txt")).saveMissions(List.of(new Todo("mission"))));

        assertEquals("Glennon could not save the mission data.", exception.getMessage());
        assertEquals("keep parent contents", Files.readString(parentPath));
        assertOnlyDataFileRemains(parentPath);
    }

    @Test
    void saveMissions_replacementFailure_preservesExistingFileAndCleansTemporaryFile()
            throws IOException, GlennonException {
        assertReplacementFailurePreservesData(new IOException("Replacement failed."),
                "Glennon could not save the mission data.");
    }

    @Test
    void saveMissions_interruptedReplacement_preservesExistingFileAndCleansTemporaryFile()
            throws IOException, GlennonException {
        assertReplacementFailurePreservesData(new InterruptedIOException("Replacement interrupted."),
                "Glennon could not save the mission data.");
    }

    @Test
    void saveMissions_atomicMoveUnavailable_preservesExistingFileWithoutFallback()
            throws IOException, GlennonException {
        assertReplacementFailurePreservesData(
                new AtomicMoveNotSupportedException("temporary", "missions", "Unsupported operation."),
                "Glennon could not save the mission data.");
    }

    @Test
    void saveMissions_replacementDenied_reportsPermissionsAndPreservesExistingFile()
            throws IOException, GlennonException {
        assertReplacementFailurePreservesData(new AccessDeniedException("missions.txt"),
                "Glennon cannot save the mission data. Check the file and folder permissions.");
    }

    @Test
    void saveMissions_failedFirstReplacement_cleansTemporaryFileWithoutCreatingData() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        IOException failure = new IOException("First replacement failed.");
        Storage storage = new Storage(dataPath) {
            @Override
            void replaceDataFile(Path temporaryPath) throws IOException {
                throw failure;
            }
        };

        GlennonException exception = assertThrows(
                GlennonException.class, () -> storage.saveMissions(List.of(new Todo("first mission"))));

        assertSame(failure, exception.getCause());
        assertFalse(Files.exists(dataPath));
        try (Stream<Path> entries = Files.list(temporaryDirectory)) {
            assertEquals(0, entries.count());
        }
    }

    @Test
    void saveMissions_runtimeReplacementFailure_preservesDataAndCleansTemporaryFile() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        String originalData = "T\t0\tdGFzaw==\n";
        Files.writeString(dataPath, originalData);
        IllegalStateException failure = new IllegalStateException("Unexpected replacement failure.");
        Storage storage = new Storage(dataPath) {
            @Override
            void replaceDataFile(Path temporaryPath) {
                throw failure;
            }
        };

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> storage.saveMissions(List.of(new Todo("replacement"))));

        assertSame(failure, exception);
        assertEquals(originalData, Files.readString(dataPath));
        assertOnlyDataFileRemains(dataPath);
    }

    @Test
    void saveMissions_cleanupFailure_retainsOriginalFailureAndSuppressedCause() throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        String originalData = "T\t0\tdGFzaw==\n";
        Files.writeString(dataPath, originalData);
        IOException failure = new IOException("Replacement failed before cleanup.");
        Storage storage = new Storage(dataPath) {
            @Override
            void replaceDataFile(Path temporaryPath) throws IOException {
                // A nonempty directory makes cleanup fail reliably without relying on file permissions.
                Files.delete(temporaryPath);
                Files.createDirectory(temporaryPath);
                Files.writeString(temporaryPath.resolve("blocker.txt"), "prevent deletion");
                throw failure;
            }
        };

        GlennonException exception = assertThrows(
                GlennonException.class, () -> storage.saveMissions(List.of(new Todo("replacement"))));

        assertEquals("Glennon could not save the mission data.", exception.getMessage());
        assertSame(failure, exception.getCause());
        assertEquals(1, failure.getSuppressed().length);
        assertInstanceOf(DirectoryNotEmptyException.class, failure.getSuppressed()[0]);
        assertEquals(originalData, Files.readString(dataPath));
    }

    @Test
    void saveMissions_directoryTargets_preservesDirectoriesAndContents() throws IOException {
        Path emptyDirectory = Files.createDirectory(temporaryDirectory.resolve("empty"));
        Path occupiedDirectory = Files.createDirectory(temporaryDirectory.resolve("occupied"));
        Path existingFile = occupiedDirectory.resolve("keep.txt");
        Files.writeString(existingFile, "keep contents");

        for (Path directory : List.of(emptyDirectory, occupiedDirectory)) {
            Storage storage = new Storage(directory);
            GlennonException exception = assertThrows(
                    GlennonException.class, () -> storage.saveMissions(List.of(new Todo("replacement"))));

            assertEquals("Glennon could not save the mission data.", exception.getMessage());
            assertTrue(Files.isDirectory(directory));
        }
        assertEquals("keep contents", Files.readString(existingFile));
    }

    @Test
    void saveMissions_symbolicLink_preservesLinkAndDestination() throws IOException {
        Path originalPath = temporaryDirectory.resolve("original.txt");
        Path linkedPath = temporaryDirectory.resolve("missions.txt");
        Files.writeString(originalPath, "T\t0\tdGFzaw==\n");
        Files.createSymbolicLink(linkedPath, originalPath.getFileName());

        GlennonException exception = assertThrows(
                GlennonException.class, () -> new Storage(linkedPath).saveMissions(List.of(new Todo("replacement"))));

        assertEquals("Glennon could not save the mission data.", exception.getMessage());
        assertTrue(Files.isSymbolicLink(linkedPath));
        assertEquals("T\t0\tdGFzaw==\n", Files.readString(originalPath));
    }

    @Test
    void saveMissions_danglingSymbolicLink_preservesLinkWithoutCreatingDestination() throws IOException {
        Path missingPath = temporaryDirectory.resolve("missing.txt");
        Path linkedPath = temporaryDirectory.resolve("missions.txt");
        Files.createSymbolicLink(linkedPath, missingPath.getFileName());

        assertThrows(
                GlennonException.class, () -> new Storage(linkedPath).saveMissions(List.of(new Todo("replacement"))));

        assertTrue(Files.isSymbolicLink(linkedPath));
        assertFalse(Files.exists(missingPath));
    }

    /**
     * Verifies that invalid stored values identify their line and leave the file unchanged.
     */
    private void assertCorruptedSecondLine(String line) throws IOException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        String corruptedData = "T\t0\tdGFzaw==\n" + line + "\n";
        Files.writeString(dataPath, corruptedData);

        GlennonException exception = assertThrows(
                GlennonException.class, () -> new Storage(dataPath).loadMissions());

        assertEquals("Mission data is corrupted at line 2.", exception.getMessage());
        assertEquals(corruptedData, Files.readString(dataPath));
    }

    /**
     * Verifies failures after a complete temporary write cannot damage the saved mission log.
     */
    private void assertReplacementFailurePreservesData(IOException failure, String expectedMessage)
            throws IOException, GlennonException {
        Path dataPath = temporaryDirectory.resolve("missions.txt");
        Storage storage = new Storage(dataPath);
        storage.saveMissions(List.of(new Todo("original")));
        byte[] originalData = Files.readAllBytes(dataPath);
        Storage failingStorage = new Storage(dataPath) {
            @Override
            void replaceDataFile(Path temporaryPath) throws IOException {
                assertEquals(List.of("T\t0\t" + StorageTest.encode("replacement")),
                        Files.readAllLines(temporaryPath));
                throw failure;
            }
        };

        GlennonException exception = assertThrows(
                GlennonException.class, () -> failingStorage.saveMissions(List.of(new Todo("replacement"))));

        assertEquals(expectedMessage, exception.getMessage());
        assertSame(failure, exception.getCause());
        assertArrayEquals(originalData, Files.readAllBytes(dataPath));
        assertEquals("original", storage.loadMissions().getFirst().getDescription());
        assertOnlyDataFileRemains(dataPath);
    }

    /**
     * Verifies that a completed or failed save leaves no temporary files behind.
     */
    private void assertOnlyDataFileRemains(Path dataPath) throws IOException {
        try (Stream<Path> entries = Files.list(temporaryDirectory)) {
            assertEquals(List.of(dataPath), entries.toList());
        }
    }

    /**
     * Encodes hand-crafted records independently of the production serializer.
     */
    private static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
}
