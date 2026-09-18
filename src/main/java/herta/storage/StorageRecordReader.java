package herta.storage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import herta.task.TaskList;

/**
 * Decodes bounded storage records using strict UTF-8 and all supported line endings.
 */
final class StorageRecordReader {
    static final int MAX_RECORD_LENGTH = 4_096;
    private static final String TOO_LONG_RECORD_ERROR = "storage record exceeds the size limit";
    private static final String TOO_MANY_RECORDS_ERROR = "storage file contains too many records";

    private StorageRecordReader() {
        // Utility class; do not instantiate.
    }

    /** Reads records without allowing malformed input or unbounded accumulation. */
    static List<String> read(Path dataFile) throws IOException {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        List<String> records = new ArrayList<>();
        try (var input = Files.newInputStream(dataFile);
             var reader = new PushbackReader(new InputStreamReader(input, decoder), 1)) {
            readBoundedRecords(reader, records);
        }
        return records;
    }

    /** Reads records while enforcing both record-length and record-count limits. */
    private static void readBoundedRecords(PushbackReader reader, List<String> records)
            throws IOException {
        StringBuilder currentRecord = new StringBuilder();
        int character;
        while ((character = reader.read()) != -1) {
            if (character == '\n' || character == '\r') {
                addRecord(records, currentRecord);
                currentRecord.setLength(0);
                skipLineFeedAfterCarriageReturn(reader, character);
                continue;
            }
            currentRecord.append((char) character);
            if (currentRecord.length() > MAX_RECORD_LENGTH) {
                throw new IOException(TOO_LONG_RECORD_ERROR);
            }
        }
        if (!currentRecord.isEmpty()) {
            addRecord(records, currentRecord);
        }
    }

    /** Consumes the second character of CRLF without hiding the next record. */
    private static void skipLineFeedAfterCarriageReturn(PushbackReader reader, int character)
            throws IOException {
        if (character != '\r') {
            return;
        }
        int nextCharacter = reader.read();
        if (nextCharacter != '\n' && nextCharacter != -1) {
            reader.unread(nextCharacter);
        }
    }

    /** Adds one record while enforcing the task-count limit. */
    private static void addRecord(List<String> records, StringBuilder currentRecord)
            throws IOException {
        records.add(currentRecord.toString());
        if (records.size() > TaskList.MAXIMUM_TASK_COUNT) {
            throw new IOException(TOO_MANY_RECORDS_ERROR);
        }
    }
}
