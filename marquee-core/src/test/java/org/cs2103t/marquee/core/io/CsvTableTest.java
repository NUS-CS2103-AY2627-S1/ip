package org.cs2103t.marquee.core.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CsvTableTest {
    private static final Path readTestFile = Path.of("src/test/resources/test_read.csv");
    private static final Path writeTestFile = Path.of("src/test/resources/test_write.csv");
    @Test
    void readFile() throws IOException {
        try {
            CsvTable testTable = CsvTable.readFile(readTestFile);
            assertIterableEquals(List.of("column1", "column \"2\"", ""), testTable.getColumns());
            assertIterableEquals(List.of(
                    testTable.createRecord(Map.of(
                            "column1", "1",
                            "column \"2\"", "2",
                            "", "3"
                    )),
                    testTable.createRecord(Map.of(
                            "column1", "\"A\"",
                            "column \"2\"", "B,B",
                            "", "C\r\nC"
                    ))
            ), testTable.getValues());
        } catch (ParseException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void writeFile() throws IOException {
        Files.write(writeTestFile, List.of(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        CsvTable testTable = new CsvTable("column#1", "Column,2", "\r\"\r\n\"\n");
        testTable.add(
                testTable.createRecord(Map.of(
                        "column#1", "field#1",
                        "Column,2", "field\"2\""
                )),
                testTable.createRecord(),
                testTable.createRecord(Map.of("column#1", "\r\n"))
        );
        CsvTable.writeFile(writeTestFile, testTable);
        assertEquals(
                "column#1,\"Column,2\",\"\r\"\"\r\n\"\"\n\"\r\n"
                        + "field#1,\"field\"\"2\"\"\",\"\"\r\n"
                        + "\"\",\"\",\"\"\r\n"
                        + "\"\r\n\",\"\",\"\"",
                Files.readString(writeTestFile)
        );
    }
}