package hampster.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hampster.parser.DateTimeParser;
import hampster.task.Deadline;
import hampster.task.Event;
import hampster.task.Task;
import hampster.task.TaskList;
import hampster.task.ToDo;

class StorageTest {

    private static final Path DATA_FILE = Path.of("data.txt");

    private static final String DEADLINE_TIME = "Jan 15 2026, 09:00 AM";
    private static final String EVENT_START = "Jan 20 2026, 02:00 PM";
    private static final String EVENT_END = "Jan 20 2026, 03:30 PM";

    @TempDir
    Path temporaryDirectory;

    private Path backupFile;
    private boolean originalFileExists;

    @BeforeEach
    void setUp() throws IOException {
        backupFile = temporaryDirectory.resolve("data.txt.backup");
        originalFileExists = Files.exists(DATA_FILE);

        if (originalFileExists) {
            Files.copy(DATA_FILE, backupFile);
        }

        Files.deleteIfExists(DATA_FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(DATA_FILE);

        if (originalFileExists) {
            Files.copy(backupFile, DATA_FILE);
        }
    }

    @Test
    void load_missingDataFile_returnsEmptyTaskList() throws IOException {
        TaskList tasks = Storage.load();

        assertEquals(0, tasks.size());
    }

    @Test
    void load_validTaskRecords_parsesAllSupportedTaskTypes() throws IOException {
        Files.write(DATA_FILE, List.of(
                "D|0|Submit CS2103T project proposal|Jan 15 2026, 09:00 AM",
                "T|0|Buy groceries for Sunday dinner",
                "E|0|Meet project team at Central Library|Jan 20 2026, 02:00 PM|Jan 20 2026, 03:30 PM",
                "T|1|Complete weekly reading assignment",
                "D|1|Renew passport before overseas trip|Jan 25 2026, 11:30 AM"
        ));

        TaskList tasks = Storage.load();

        assertEquals(5, tasks.size());

        assertEquals(
                "D|0|Submit CS2103T project proposal|Jan 15 2026, 09:00 AM|",
                tasks.get(0).saveString()
        );

        assertEquals(
                "T|0|Buy groceries for Sunday dinner|",
                tasks.get(1).saveString()
        );

        assertEquals(
                "E|0|Meet project team at Central Library|Jan 20 2026, 02:00 PM|Jan 20 2026, 03:30 PM|",
                tasks.get(2).saveString()
        );

        assertEquals(
                "T|1|Complete weekly reading assignment|",
                tasks.get(3).saveString()
        );

        assertEquals(
                "D|1|Renew passport before overseas trip|Jan 25 2026, 11:30 AM|",
                tasks.get(4).saveString()
        );
    }

    @Test
    void load_blankLinesBetweenRecords_ignoresBlankLines() throws IOException {
        Files.write(DATA_FILE, List.of(
                "",
                "T|0|Prepare slides for Monday presentation",
                "",
                "",
                "D|0|Submit application before registration closes|Jan 18 2026, 05:00 PM",
                ""
        ));

        TaskList tasks = Storage.load();

        assertEquals(2, tasks.size());
        assertEquals(
                "T|0|Prepare slides for Monday presentation|",
                tasks.get(0).saveString()
        );
        assertEquals(
                "D|0|Submit application before registration closes|Jan 18 2026, 05:00 PM|",
                tasks.get(1).saveString()
        );
    }

    @Test
    void load_malformedTaskRecords_skipsInvalidRecords() throws IOException {
        Files.write(DATA_FILE, List.of(
                "T|0|Attend tutorial consultation",
                "D|0|Renew student pass",
                "D|0|Impossible date|Feb 30 2026, 05:00 PM",
                "E|0|Group project meeting|Jan 20 2026, 02:00 PM",
                "X|0|Unsupported task type",
                "T|1|Complete database revision"
        ));

        TaskList tasks = Storage.load();

        assertEquals(2, tasks.size());
        assertEquals(
                "T|0|Attend tutorial consultation|",
                tasks.get(0).saveString()
        );
        assertEquals(
                "T|1|Complete database revision|",
                tasks.get(1).saveString()
        );
    }

    @Test
    void save_validTaskList_writesExpectedRecordsToDataFile() throws Exception {
        List<Task> tasks = new ArrayList<>();

        tasks.add(new ToDo(false, "Buy groceries for Sunday dinner"));
        tasks.add(new Deadline(
                false,
                "Submit CS2103T project proposal",
                DateTimeParser.parseFromSave(DEADLINE_TIME)
        ));
        tasks.add(new Event(
                false,
                "Meet project team at Central Library",
                DateTimeParser.parseFromSave(EVENT_START),
                DateTimeParser.parseFromSave(EVENT_END)
        ));

        Storage.save(tasks);

        assertEquals(
                List.of(
                        "T|0|Buy groceries for Sunday dinner|",
                        "D|0|Submit CS2103T project proposal|Jan 15 2026, 09:00 AM|",
                        "E|0|Meet project team at Central Library|Jan 20 2026, 02:00 PM|Jan 20 2026, 03:30 PM|"
                ),
                Files.readAllLines(DATA_FILE)
        );
    }

    @Test
    void saveThenLoad_validTaskList_preservesTaskData() throws Exception {
        List<Task> originalTasks = List.of(
                new ToDo(true, "Complete weekly reading assignment"),
                new Deadline(
                        false,
                        "Renew passport before overseas trip",
                        DateTimeParser.parseFromSave(DEADLINE_TIME)
                ),
                new Event(
                        false,
                        "Attend internship interview",
                        DateTimeParser.parseFromSave(EVENT_START),
                        DateTimeParser.parseFromSave(EVENT_END)
                )
        );

        Storage.save(originalTasks);

        TaskList loadedTasks = Storage.load();

        assertEquals(originalTasks.size(), loadedTasks.size());

        for (int i = 0; i < originalTasks.size(); i++) {
            assertEquals(
                    originalTasks.get(i).saveString(),
                    loadedTasks.get(i).saveString()
            );
        }
    }
}
