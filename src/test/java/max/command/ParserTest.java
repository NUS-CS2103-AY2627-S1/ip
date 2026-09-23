package max.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import max.maxexception.EmptyDescriptionException;
import max.maxexception.DateBeforeCurrentDateException;
import max.maxexception.InvalidTaskIDException;
import max.maxexception.MaxException;
import max.maxexception.MissingDatesException;

public class ParserTest {
  
  @Test 
  public void getCommandWord_singleWord_returnsWholeWord() {
    assertEquals("list", Parser.getCommandWord("list"));
  }

  @Test
  public void getCommandWord_nullOrBlank_returnsEmptyString() {
    assertEquals("", Parser.getCommandWord(null));
    assertEquals("", Parser.getCommandWord("   "));
  }

  @Test 
  public void getCommandWord_wordWithArguments_returnFirstWordOnly() {
    assertEquals("todo", Parser.getCommandWord("todo read book"));
  }

  @Test 
  public void getCommandWord_multipleSpacesBetweenWords_returnsFirstWord() {
    assertEquals("deadline", Parser.getCommandWord("deadline return book /by 2026-08-27"));
  }

  @Test 
  public void getArguments_validInput_returnsArgumentsAfterCommand() throws EmptyDescriptionException {
    assertEquals("read book", Parser.getArguments("todo read book"));
  }

  @Test
  public void getArguments_nullInput_throwsException() {
    assertThrows(EmptyDescriptionException.class, () -> Parser.getArguments(null));
  }

  @Test 
  public void getArguments_noArguments_exceptionThrown() {
    assertThrows(EmptyDescriptionException.class, () -> Parser.getArguments("todo"));
  }

  @Test 
  public void getArguments_argumentsWithMultipleSpaces() throws EmptyDescriptionException {
    assertEquals("read book /by 2026-08-27", Parser.getArguments("deadline read book /by 2026-08-27"));
  }

  @Test 
  public void parseDeadline_validInput() throws MissingDatesException {
    String[] result = Parser.parseDeadline("read book /by 2026-08-27");
    assertArrayEquals(new String[] {"read book ", "2026-08-27"}, result);
  }

  @Test 
  public void parseDeadline_missingByKeyword() {
    assertThrows(MissingDatesException.class, () -> Parser.parseDeadline("read book 2026-08-27"));
  }

  @Test 
  public void parseDeadline_emptyString() {
    assertThrows(MissingDatesException.class, () -> Parser.parseDeadline(""));
  }

  @Test 
  public void parseEvent_validInput() throws MissingDatesException {
    String[] result = Parser.parseEvent("meeting /from 2026-08-27 /to 2026-08-28");
    assertArrayEquals(new String[] {"meeting ", "2026-08-27", "2026-08-28"}, result);
  } 

  @Test
  public void parseEvent_missingToKeyword() {
    assertThrows(MissingDatesException.class,
                    () -> Parser.parseEvent("project meeting /from 2026-08-27 2026-08-28"));
  }

  @Test 
  public void parseEvent_missingFromKeyword() {
    assertThrows(MissingDatesException.class,
                    () -> Parser.parseEvent("project meeting 2026-08-27 /to 2026-08-28"));
  }

  @Test 
  public void parseEvent_missingBothKeyword() {
    assertThrows(MissingDatesException.class,
                    () -> Parser.parseEvent("project meeting 2026-08-27 2026-08-28"));
  }

  @Test 
  public void parseIndex_validPositiveNumber() throws InvalidTaskIDException {
    assertEquals(2, Parser.parseIndex("2"));
  }

  @Test 
  public void parseIndex_numberWithSpaces() throws InvalidTaskIDException {
    assertEquals(2, Parser.parseIndex("2"));
  }

  @Test 
  public void parseIndex_nonNumericString() {
    assertThrows(InvalidTaskIDException.class, () -> Parser.parseIndex("as"));
  }

  @Test 
  public void parseIndex_emptyString() {
    assertThrows(InvalidTaskIDException.class, () -> Parser.parseIndex(""));
  }

  @Test 
  public void parseDate_validIsoDate() throws MaxException {
    assertEquals(LocalDate.of(2026, 8, 27), Parser.parseDate("2026-08-27"));
  }
  
  @Test 
  public void parseDate_dateWithSpace() throws MaxException {
    assertEquals(LocalDate.of(2026, 8, 27), Parser.parseDate(" 2026-08-27"));
  }

  @Test 
  public void parseDate_wrongFormat() {
    assertThrows(MaxException.class, () -> Parser.parseDate("2026/08/27"));
  }

  @Test 
  public void parseDate_nonDate() {
    assertThrows(MaxException.class, () -> Parser.parseDate("this is not a date"));
  }

  @Test 
  public void parseDate_emptyDate(){
    assertThrows(MaxException.class, () -> Parser.parseDate(""));
  }
  @Test 
  public void parseDate_invalidDate() {
    assertThrows(MaxException.class, () -> Parser.parseDate("2026-13-50"));
  }

  @Test
  public void parseDateTime_validDateAndTime() throws MaxException {
    assertEquals(LocalDateTime.of(2026, 8, 27, 14, 30),
            Parser.parseDateTime("2026-08-27 14:30"));
  }

  @Test
  public void parseDateTime_dateOnlyDefaultsToMidnight() throws MaxException {
    assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0),
            Parser.parseDateTime("2026-08-27"));
  }

  @Test
  public void validateDateTimeAfterCurrentDate_acceptsTodayAndFuture() throws MaxException {
    Parser.validateDateTimeAfterCurrentDate(LocalDate.now().atStartOfDay());
    Parser.validateDateTimeAfterCurrentDate(LocalDate.now().plusDays(1).atStartOfDay());
  }

  @Test
  public void validateDateTimeAfterCurrentDate_rejectsPastDate() {
    DateBeforeCurrentDateException exception = assertThrows(
            DateBeforeCurrentDateException.class,
            () -> Parser.validateDateTimeAfterCurrentDate(
                    LocalDate.now().minusDays(1).atStartOfDay()));
    assertEquals("Invalid date. The date cannot be before the current date.",
            exception.getMessage());
  }
  
}
