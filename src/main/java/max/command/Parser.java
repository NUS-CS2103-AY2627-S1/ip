package max.command;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import max.maxexception.EmptyDescriptionException;
import max.maxexception.DateBeforeCurrentDateException;
import max.maxexception.InvalidTaskIDException;
import max.maxexception.MaxException;
import max.maxexception.MissingDatesException;
import max.maxexception.InvalidDateException;


public class Parser {

  /**
   * Get the command from a given String.
   *
   * @param input String of the input where command is splice from.
   * @return String the command.
   *
   * @example
   * ```
   * Parser.getCommandWord("todo read book")
   * returns todo
   * ```
   */

  public static String getCommandWord(String input) {
    if (input == null || input.trim().isEmpty()) {
      return "";
    }
    return input.trim().split("\\s+", 2)[0];
  }

  /**
   * Get arguments base on the given input.
   *
   * @param input string where the arguments are retrieved from  
   * @return String arguments of the string 
   *
   */

  public static String getArguments(String input) throws EmptyDescriptionException {
    if (input == null) {
      throw new EmptyDescriptionException();
    }
    String trimmed = input.trim();
    String[] parts = trimmed.split("\\s+", 2);
    if (parts.length < 2 || parts[1].trim().isEmpty()) {
      throw new EmptyDescriptionException();
    }
    return parts[1].trim();
  }

  /**
   * A one-line summary.
   *
   * Description.
   *
   * @param name  Type and description of the parameter.
   * @return Type and description of the returned object.
   *
   * @example
   * ```
   * Write me later
   * ```
   */

  public static String[] parseDeadline(String args) throws MissingDatesException {
    String[] ddl = args == null ? new String[0] : args.split("/by\\s+", -1);
    if (ddl.length != 2 || ddl[0].trim().isEmpty() || ddl[1].trim().isEmpty()) {
      throw new MissingDatesException();
    }
    assert ddl.length >= 2 : "A valid deadline split has a description and date";
    return ddl;
  }

  /**
   * Split the arguments of event command into relevant segments.
   *
   * @param args arguments to be split  
   * @return String array of the arguments.
   *
   */
  public static String[] parseEvent(String args) throws MissingDatesException {
    String[] evt = args == null ? new String[0] : args.split("/from\\s+", -1);
    if (evt.length != 2 || evt[0].trim().isEmpty()) {
      throw new MissingDatesException();
    }
    String[] startEnd = evt[1].split("\\s+/to\\s+", -1);
    if (startEnd.length != 2 || startEnd[0].trim().isEmpty() || startEnd[1].trim().isEmpty()) {
      throw new MissingDatesException();
    }
    assert evt.length >= 2 : "A valid event split has a start marker";
    assert startEnd.length >= 2 : "A valid event split has an end marker";
    return new String[] {evt[0], startEnd[0], startEnd[1]};
  }

  /**
   * Convert string to integer.
   *
   * @param args string to be connverted to int .
   * @return int representation of string.
   *
   */
  public static int parseIndex(String str) throws InvalidTaskIDException {
    try {
      return Integer.parseInt(str.trim());
    } catch (NumberFormatException e) {
      throw new InvalidTaskIDException();
    }
  }


  /**
   * Convert date representation from string to LocalDate
   *
   * @param dateStr  string representation of date 
   * @return LocalDate LocalDate representation of date string 
   *
   */
  public static LocalDate parseDate(String dateStr) throws MaxException {
    try {
      return LocalDate.parse(dateStr.trim());
    } catch (DateTimeParseException | NullPointerException e) {
      throw new InvalidDateException();
    }
  }

  /**
   * Converts a date or date-time string to a LocalDateTime. Date-only values
   * are interpreted at midnight for backwards compatibility.
   */
  public static LocalDateTime parseDateTime(String dateTimeStr) throws MaxException {
    if (dateTimeStr == null) {
      throw new InvalidDateException();
    }
    String value = dateTimeStr.trim();
    try {
      return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    } catch (DateTimeParseException e) {
      try {
        return LocalDate.parse(value).atStartOfDay();
      } catch (DateTimeParseException dateException) {
        throw new InvalidDateException();
      }
    }
  }

  /**
   * Ensures that a task date is not earlier than today's date.
   *
   * @param dateTime date-time supplied for a deadline or event
   * @throws DateBeforeCurrentDateException if the date is before today
   */
  public static void validateDateTimeAfterCurrentDate(LocalDateTime dateTime)
          throws DateBeforeCurrentDateException {
    if (dateTime == null || dateTime.toLocalDate().isBefore(LocalDate.now())) {
      throw new DateBeforeCurrentDateException();
    }
  }
}
