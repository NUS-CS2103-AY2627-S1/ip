package botavius.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import botavius.exception.BotaviusException;

/** Reads and writes the task data file used by Botavius. */
public class Storage {
    /** Path of the file used for persistence. */
    private String filePath;
    /** Creates storage backed by the specified file.
     *
     * @param filePath path of the persistence file
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }
    /**
     * Writes task data to the configured file.
     *
     * @param saveData serialized task data to write
     * @return {@code saved!} after the write succeeds
     * @throws BotaviusException if the task data cannot be written
     */
    public String save(String saveData) {
        //i didnt want to do an exception check but i am forced to.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath))) {
            writer.write(saveData.toString());
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            throw new BotaviusException("could not save tasks: " + e.getMessage());
        }
        return "saved!";
    }

    /**
     * Reads all text from the configured file.
     *
     * @return file contents, or an empty string when reading fails
     */
    public String load() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            return br.readAllAsString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}
