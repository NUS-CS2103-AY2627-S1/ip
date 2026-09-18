package herta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import herta.parser.CommandType;

/**
 * Tests the mapping from parser command types to presentation categories.
 */
class ResponseCategoryTest {
    private static final Map<CommandType, ResponseCategory> EXPECTED_CATEGORIES = createExpectedCategories();

    @Test
    void fromCommandType_everyCommandType_returnsTruthfulCategory() {
        for (Map.Entry<CommandType, ResponseCategory> entry : EXPECTED_CATEGORIES.entrySet()) {
            assertEquals(entry.getValue(), ResponseCategory.fromCommandType(entry.getKey()));
        }
    }

    @Test
    void fromCommandType_nullCommandType_rejectsInput() {
        assertThrows(NullPointerException.class, () -> ResponseCategory.fromCommandType(null));
    }

    private static Map<CommandType, ResponseCategory> createExpectedCategories() {
        Map<CommandType, ResponseCategory> categories = new EnumMap<>(CommandType.class);
        categories.put(CommandType.TODO, ResponseCategory.ADD);
        categories.put(CommandType.DEADLINE, ResponseCategory.ADD);
        categories.put(CommandType.EVENT, ResponseCategory.ADD);
        categories.put(CommandType.MARK, ResponseCategory.MARK);
        categories.put(CommandType.UNMARK, ResponseCategory.UNMARK);
        categories.put(CommandType.DELETE, ResponseCategory.DELETE);
        categories.put(CommandType.ARCHIVE, ResponseCategory.ARCHIVE);
        categories.put(CommandType.RESTORE, ResponseCategory.RESTORE);
        categories.put(CommandType.LIST, ResponseCategory.QUERY);
        categories.put(CommandType.FIND, ResponseCategory.QUERY);
        categories.put(CommandType.FILTER, ResponseCategory.QUERY);
        categories.put(CommandType.UPCOMING, ResponseCategory.QUERY);
        categories.put(CommandType.SORT, ResponseCategory.QUERY);
        categories.put(CommandType.ARCHIVED, ResponseCategory.QUERY);
        categories.put(CommandType.BYE, ResponseCategory.EXIT);
        categories.put(CommandType.UNKNOWN, ResponseCategory.ERROR);
        return categories;
    }
}
