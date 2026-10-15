use chrono::NaiveDateTime;

/// A command accepted by the interactive task manager.
#[derive(Debug, PartialEq)]
pub enum Command {
    Bye,
    List,
    Mark {
        number: usize,
        done: bool,
    },
    Delete {
        number: usize,
    },
    Find {
        keyword: String,
    },
    Todo {
        description: String,
    },
    Deadline {
        description: String,
        by: NaiveDateTime,
    },
    Event {
        description: String,
        from: NaiveDateTime,
        to: NaiveDateTime,
    },
}

/// User-facing errors produced while parsing a command.
#[derive(Debug, PartialEq)]
pub enum ParseError {
    EmptyTodo,
    InvalidDeadline,
    InvalidEvent,
    EmptyFind,
    InvalidTaskNumber,
    InvalidDate,
    UnknownCommand,
}

impl ParseError {
    pub fn message(&self) -> &'static str {
        match self {
            Self::EmptyTodo => "A todo needs a description.",
            Self::InvalidDeadline => "A deadline needs a description and a /by date or time.",
            Self::InvalidEvent => "An event needs a description, /from time, and /to time.",
            Self::EmptyFind => "Please provide a keyword to search for.",
            Self::InvalidTaskNumber => "Please provide a valid task number.",
            Self::InvalidDate => "Use dates and times like 2019-12-02 18:00.",
            Self::UnknownCommand => {
                "I don't recognise that command. Try todo, deadline, event, list, mark, or unmark."
            }
        }
    }
}

pub fn parse(input: &str) -> Result<Command, ParseError> {
    if input == "bye" {
        return Ok(Command::Bye);
    }
    if input == "list" {
        return Ok(Command::List);
    }
    if let Some(number) = input.strip_prefix("mark ") {
        return parse_number(number).map(|number| Command::Mark { number, done: true });
    }
    if let Some(number) = input.strip_prefix("unmark ") {
        return parse_number(number).map(|number| Command::Mark {
            number,
            done: false,
        });
    }
    if let Some(number) = input.strip_prefix("delete ") {
        return parse_number(number).map(|number| Command::Delete { number });
    }
    if let Some(keyword) = input.strip_prefix("find ") {
        let keyword = keyword.trim();
        return if keyword.is_empty() {
            Err(ParseError::EmptyFind)
        } else {
            Ok(Command::Find {
                keyword: keyword.to_owned(),
            })
        };
    }
    if input == "todo" || input.starts_with("todo ") {
        let description = input.strip_prefix("todo").unwrap_or_default().trim();
        return if description.is_empty() {
            Err(ParseError::EmptyTodo)
        } else {
            Ok(Command::Todo {
                description: description.to_owned(),
            })
        };
    }
    if let Some(body) = input.strip_prefix("deadline ") {
        return parse_deadline(body);
    }
    if let Some(body) = input.strip_prefix("event ") {
        return parse_event(body);
    }
    Err(ParseError::UnknownCommand)
}

fn parse_number(text: &str) -> Result<usize, ParseError> {
    text.trim()
        .parse()
        .map_err(|_| ParseError::InvalidTaskNumber)
}

fn parse_date(text: &str) -> Result<NaiveDateTime, ParseError> {
    NaiveDateTime::parse_from_str(text, "%Y-%m-%d %H:%M").map_err(|_| ParseError::InvalidDate)
}

fn parse_deadline(body: &str) -> Result<Command, ParseError> {
    let Some((description, by)) = body.split_once(" /by ") else {
        return Err(ParseError::InvalidDeadline);
    };
    let (description, by) = (description.trim(), by.trim());
    if description.is_empty() || by.is_empty() {
        return Err(ParseError::InvalidDeadline);
    }
    Ok(Command::Deadline {
        description: description.to_owned(),
        by: parse_date(by)?,
    })
}

fn parse_event(body: &str) -> Result<Command, ParseError> {
    let Some((description, interval)) = body.split_once(" /from ") else {
        return Err(ParseError::InvalidEvent);
    };
    let Some((from, to)) = interval.split_once(" /to ") else {
        return Err(ParseError::InvalidEvent);
    };
    let (description, from, to) = (description.trim(), from.trim(), to.trim());
    if description.is_empty() || from.is_empty() || to.is_empty() {
        return Err(ParseError::InvalidEvent);
    }
    Ok(Command::Event {
        description: description.to_owned(),
        from: parse_date(from)?,
        to: parse_date(to)?,
    })
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parses_todo() {
        assert_eq!(
            parse("todo read a book"),
            Ok(Command::Todo {
                description: "read a book".to_owned()
            })
        );
    }

    #[test]
    fn rejects_incomplete_event() {
        assert_eq!(
            parse("event meeting /from 2026-09-10 12:00"),
            Err(ParseError::InvalidEvent)
        );
    }
}
