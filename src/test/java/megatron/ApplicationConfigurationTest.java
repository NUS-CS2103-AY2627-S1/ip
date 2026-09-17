package megatron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests command-line argument handling by {@link ApplicationConfiguration}.
 */
class ApplicationConfigurationTest {
    @Test
    void fromArguments_noArguments_usesDefaultStorageFile() {
        ApplicationConfiguration configuration = ApplicationConfiguration.fromArguments(List.of());

        assertEquals("data/megatron.csv", configuration.getStorageFilePath());
    }

    @Test
    void fromArguments_storageFileArgument_usesSuppliedStorageFile() {
        ApplicationConfiguration configuration = ApplicationConfiguration.fromArguments(
                new String[] {"data/another-file.csv"});

        assertEquals("data/another-file.csv", configuration.getStorageFilePath());
    }

    @Test
    void fromArguments_additionalArguments_usesOnlyFirstArgument() {
        ApplicationConfiguration configuration = ApplicationConfiguration.fromArguments(
                List.of("data/first.csv", "data/ignored.csv"));

        assertEquals("data/first.csv", configuration.getStorageFilePath());
    }

    @Test
    void fromArguments_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ApplicationConfiguration.fromArguments((List<String>) null));
    }

    @Test
    void fromArguments_nullArray_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ApplicationConfiguration.fromArguments((String[]) null));
    }
}
