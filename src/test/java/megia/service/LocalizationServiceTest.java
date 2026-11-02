package megia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import megia.exception.ErrorCode;

class LocalizationServiceTest {
    private static final String TASK_NOT_FOUND_TEMPLATE =
            "Task %d does not exist. Choose a number from 1 to %d, or use \"list\" to view your tasks.";

    @BeforeEach
    void useEnglishLanguage() {
        LocalizationService.setLanguage("en");
    }

    @AfterEach
    void restoreEnglishLanguage() {
        LocalizationService.setLanguage("en");
    }

    @Test
    void getException_withoutArguments_returnsLocalizedMessage() {
        assertEquals(
                "A todo needs a description. Try: todo borrow a book",
                LocalizationService.getException(ErrorCode.TODO_DESCRIPTION_MISSING));
    }

    @Test
    void getException_withOneArgument_formatsLocalizedMessage() {
        assertEquals(
                "\"launch\" is not a recognized command. Enter \"help\" for all commands and "
                        + "accepted formats.",
                LocalizationService.getException(ErrorCode.UNKNOWN_COMMAND, "launch"));
    }

    @Test
    void getException_withRepeatedArgument_reusesIndexedPlaceholder() {
        assertEquals(
                "Enter the task number to mark. Try: mark 1",
                LocalizationService.getException(ErrorCode.TASK_ID_MISSING, "mark"));
    }

    @Test
    void getException_withMultipleArguments_formatsTypedValues() {
        assertEquals(
                "Task 3 does not exist. Choose a number from 1 to 2, or use \"list\" to view your tasks.",
                LocalizationService.getException(ErrorCode.TASK_NOT_FOUND, 3, 2));
    }

    @Test
    void getException_withExtraArguments_ignoresTrailingValues() {
        assertEquals(
                LocalizationService.getException(ErrorCode.UNKNOWN_COMMAND, "launch"),
                LocalizationService.getException(ErrorCode.UNKNOWN_COMMAND, "launch", "ignored"));
    }

    @Test
    void getException_withMissingOrIncompatibleArguments_returnsTemplate() {
        assertEquals(
                TASK_NOT_FOUND_TEMPLATE,
                LocalizationService.getException(ErrorCode.TASK_NOT_FOUND, 3));
        assertEquals(
                TASK_NOT_FOUND_TEMPLATE,
                LocalizationService.getException(ErrorCode.TASK_NOT_FOUND, "three", 2));
    }

    @Test
    void getException_withNullArgumentArray_treatsItAsEmpty() {
        assertEquals(
                LocalizationService.getException(ErrorCode.TASK_ID_NOT_POSITIVE),
                LocalizationService.getException(ErrorCode.TASK_ID_NOT_POSITIVE, (Object[]) null));
        assertTrue(LocalizationService.getException(ErrorCode.TASK_ID_MISSING, (Object[]) null)
                .contains("%1$s"));
    }

    @Test
    void getException_withChineseLanguage_formatsLocalizedMessage() {
        LocalizationService.setLanguage("cn");

        String message = LocalizationService.getException(ErrorCode.DUPLICATE_MARKER, "/by");

        assertTrue(message.contains("/by"));
        assertTrue(message.contains("只能出现一次"));
    }

    @Test
    void getException_storageWriteFailure_isLocalizedInEnglishAndChinese() {
        String englishMessage = LocalizationService.getException(
                ErrorCode.STORAGE_WRITE_FAILED, "tasks.csv");

        LocalizationService.setLanguage("cn");
        String chineseMessage = LocalizationService.getException(
                ErrorCode.STORAGE_WRITE_FAILED, "tasks.csv");

        assertTrue(englishMessage.contains("tasks.csv"));
        assertTrue(chineseMessage.contains("tasks.csv"));
        assertFalse(englishMessage.equals(chineseMessage));
    }

    @Test
    void getMessage_help_isLocalizedInEnglishAndChinese() {
        assertTrue(LocalizationService.getMessage("greeting").contains("\"help\""));
        assertTrue(LocalizationService.getMessage("help").contains("Available commands:"));
        assertTrue(LocalizationService.getMessage("help").contains("deadline <description>"));
        assertTrue(LocalizationService.getMessage("help")
                .contains("event <description> /from <date and time> /to <date and time>"));
        assertTrue(LocalizationService.getMessage("help")
                .contains("Dates: YYYY-MM-DD or D/M/YYYY. Times: 24-hour HHmm."));

        LocalizationService.setLanguage("cn");

        assertTrue(LocalizationService.getMessage("greeting").contains("\"help\""));
        assertTrue(LocalizationService.getMessage("help").contains("可用指令："));
        assertTrue(LocalizationService.getMessage("help").contains("deadline <任务说明>"));
        assertTrue(LocalizationService.getMessage("help")
                .contains("event <任务说明> /from <日期和时间> /to <日期和时间>"));
        assertTrue(LocalizationService.getMessage("help")
                .contains("日期：YYYY-MM-DD 或 D/M/YYYY。时间：24 小时制 HHmm。"));
    }

    @Test
    void getMessage_studyCompanionVoice_preservesPlaceholdersAndCounts() {
        assertEquals("Hi, I'm Megia, your study companion. Type \"help\" whenever you need a hand.",
                LocalizationService.getMessage("greeting"));
        assertEquals("All set. I've added this task:",
                LocalizationService.getMessage("task_storage_add"));
        assertEquals("Your task list now has %d tasks.",
                LocalizationService.getMessage("task_storage_add_2"));
        assertEquals("No tasks are scheduled for %s.",
                LocalizationService.getMessage("task_storage_date_empty"));
        assertEquals("I couldn't find any tasks containing \"%s\".",
                LocalizationService.getMessage("task_storage_find_empty"));
        assertEquals("All set. I've removed this task:",
                LocalizationService.getMessage("task_storage_delete"));
        assertEquals("Good progress. I've marked this task as complete:",
                LocalizationService.getMessage("task_storage_mark"));
        assertEquals("No problem. I've marked this task as incomplete:",
                LocalizationService.getMessage("task_storage_unmark"));
        assertEquals("Goodbye. Keep going one task at a time.",
                LocalizationService.getMessage("farewell"));
        assertEquals("Your task list now has 3 tasks.", String.format(
                LocalizationService.getMessage("task_storage_add_2"), 3));
        assertEquals("No tasks are scheduled for 2026-09-17.", String.format(
                LocalizationService.getMessage("task_storage_date_empty"), "2026-09-17"));

        LocalizationService.setLanguage("cn");

        assertEquals("你好！我是 Megia，你的学习伙伴。需要帮助时，输入 \"help\"。",
                LocalizationService.getMessage("greeting"));
        assertEquals("好的，已添加这项任务：",
                LocalizationService.getMessage("task_storage_add"));
        assertEquals("你的任务清单现在有 %d 项任务。",
                LocalizationService.getMessage("task_storage_add_2"));
        assertEquals("%s 没有安排任务。",
                LocalizationService.getMessage("task_storage_date_empty"));
        assertEquals("没有找到包含“%s”的任务。",
                LocalizationService.getMessage("task_storage_find_empty"));
        assertEquals("好的，已移除这项任务：",
                LocalizationService.getMessage("task_storage_delete"));
        assertEquals("做得很好，这项任务已完成：",
                LocalizationService.getMessage("task_storage_mark"));
        assertEquals("没关系，这项任务已恢复为未完成：",
                LocalizationService.getMessage("task_storage_unmark"));
        assertEquals("再见。继续加油，一次完成一项任务。",
                LocalizationService.getMessage("farewell"));
        assertEquals("你的任务清单现在有 3 项任务。", String.format(
                LocalizationService.getMessage("task_storage_add_2"), 3));
        assertEquals("2026-09-17 没有安排任务。", String.format(
                LocalizationService.getMessage("task_storage_date_empty"), "2026-09-17"));
    }
}
