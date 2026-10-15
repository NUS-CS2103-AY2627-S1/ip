package zinc.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the shared command-reference content. */
public class HelpContentTest {
    @Test
    public void getTopics_allTopics_haveRequiredDetails() {
        List<HelpTopic> topics = HelpContent.getTopics();

        assertEquals(15, topics.size());
        assertTrue(topics.stream().allMatch(topic -> !topic.getTitle().isBlank()));
        assertTrue(topics.stream().allMatch(topic -> !topic.getCommand().isBlank()));
        assertTrue(topics.stream().allMatch(topic -> !topic.getDescription().isBlank()));
        assertTrue(topics.stream().allMatch(topic -> !topic.getUsages().isEmpty()));
    }

    @Test
    public void getTopics_contactListTopic_includesEveryAlias() {
        HelpTopic contactListTopic = HelpContent.getTopics().stream()
                .filter(topic -> topic.getCommand().equals("contact list"))
                .findFirst()
                .orElseThrow();

        assertEquals(List.of("contact ls", "ct list", "ct ls"), contactListTopic.getAliases());
        assertEquals(List.of("contact list [keyword]"), contactListTopic.getUsages());
    }

    @Test
    public void getTopics_returnedCollections_cannotBeModified() {
        List<HelpTopic> topics = HelpContent.getTopics();

        assertThrows(UnsupportedOperationException.class, () -> topics.clear());
        assertThrows(UnsupportedOperationException.class, () -> topics.get(0).getUsages().clear());
    }

    @Test
    public void getCompactOverview_allCategories_omitsDetailedUsage() {
        String overview = HelpContent.getCompactOverview();

        assertTrue(overview.contains("Tasks:"));
        assertTrue(overview.contains("Contacts:"));
        assertTrue(overview.contains("General:"));
        assertTrue(overview.contains("contact list"));
        assertFalse(overview.contains("<description>"));
    }
}
