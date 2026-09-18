package korvus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Contains the configuration setting for the bot.
 */
public class Config {
    private enum ConfigType {
        tasklist_file_path("tasklist.txt"),
        datetime_format("yyyy-MM-dd HHmm"),
        commandline_length("100");

        private String value;

        ConfigType(String value) {
            this.value = value;
        }
    }

    public static final String CONFIG_FILE = "korvus.config"; //Using Hashmap

    private HashMap<String, String> configMap;

    /**
     * Returns an instance of a Config object.
     */
    private Config() {
        this.configMap = new HashMap<>();
    }

    /**
     * Returns the value corresponding to the provided config name.
     *
     * @param configName The config name to be searched.
     * @return String value of the config name.
     */
    public String getValue(String configName) {
        return configMap.get(configName);
    }

    /**
     * Sets the config name to the corresponding config value.
     *
     * @param configName The config name to be edited.
     * @param configValue The config value to be added.
     * @return String value of the previous config value (if any).
     */
    public String setValue(String configName, String configValue) {
        return configMap.put(configName, configValue);
    }

    /**
     * Generates a new Config object with default values.
     *
     * @return Config object with default values.
     */
    public static Config generateNewConfig() {
        Config newConfig = new Config();

        Arrays.stream(ConfigType.values())
                .forEach((ct) -> newConfig.configMap.put(ct.toString(), ct.value));

        return newConfig;
    }

    /**
     * Returns the Config object from reading from the provided reader.
     * If there are any missing values in said Config object,
     * adds the default values into the Config object instead.
     *
     * @param br BufferedReader pointing to the Config file to be read.
     * @return Config object with default values.
     */
    public static Config readConfigFile(BufferedReader br) {
        assert br != null;

        Config config = new Config();
        br.lines()
                .map(str -> str.split("="))
                .filter(arr -> arr.length == 2)
                .forEach(arr -> config.configMap.put(arr[0], arr[1]));

        Arrays.stream(ConfigType.values())
                .filter((ct) -> !config.configMap.containsKey(ct.toString()))
                .forEach((ct) -> config.configMap.put(ct.toString(), ct.value));

        return config;
    }

    /**
     * Saves the Config object into a file.
     *
     * @param bw BufferedWriter pointing to the Config file to write to.
     */
    public void saveConfigFile(BufferedWriter bw) throws IOException {
        assert bw != null;

        String output = configMap.keySet().stream()
                .map((key) -> String.format("%s=%s", key, configMap.get(key)))
                .reduce("", (a, x) -> String.format("%s\n%s", a, x));

        bw.write(output);
        bw.close();
    }
}
