use std::fs;
use std::io;
use std::path::PathBuf;

use chrono::NaiveDateTime;

use crate::task::{Task, TaskKind};

/// Loads and saves tasks using the same format as the Java implementation.
pub struct Storage {
    path: PathBuf,
}

impl Storage {
    pub fn new(path: PathBuf) -> Self {
        Self { path }
    }

    pub fn load(&self) -> io::Result<Vec<Task>> {
        if !self.path.exists() {
            return Ok(Vec::new());
        }
        let contents = fs::read_to_string(&self.path)?;
        contents.lines().try_fold(Vec::new(), |mut tasks, line| {
            if let Some(task) = parse_task(line)? {
                tasks.push(task);
            }
            Ok(tasks)
        })
    }

    pub fn save(&self, tasks: &[Task]) -> io::Result<()> {
        if let Some(parent) = self.path.parent() {
            fs::create_dir_all(parent)?;
        }
        let mut contents = tasks
            .iter()
            .map(Task::to_save_string)
            .collect::<Vec<_>>()
            .join("\n");
        if !tasks.is_empty() {
            contents.push('\n');
        }
        fs::write(&self.path, contents)
    }
}

fn parse_task(line: &str) -> io::Result<Option<Task>> {
    let parts = line.split(" | ").collect::<Vec<_>>();
    let (kind, done, description) = match parts.as_slice() {
        ["T", done, description] => (TaskKind::Todo, *done, *description),
        ["D", done, description, by] => (
            TaskKind::Deadline {
                by: parse_saved_date(by)?,
            },
            *done,
            *description,
        ),
        ["E", done, description, interval] => {
            let Some((from, to)) = interval.split_once(" -> ") else {
                return Ok(None);
            };
            (
                TaskKind::Event {
                    from: parse_saved_date(from)?,
                    to: parse_saved_date(to)?,
                },
                *done,
                *description,
            )
        }
        _ => return Ok(None),
    };
    if done != "0" && done != "1" {
        return Ok(None);
    }
    Ok(Some(Task {
        description: description.to_owned(),
        is_done: done == "1",
        kind,
    }))
}

fn parse_saved_date(text: &str) -> io::Result<NaiveDateTime> {
    [
        "%Y-%m-%dT%H:%M",
        "%Y-%m-%dT%H:%M:%S",
        "%Y-%m-%dT%H:%M:%S%.f",
    ]
    .iter()
    .find_map(|format| NaiveDateTime::parse_from_str(text, format).ok())
    .ok_or_else(|| io::Error::new(io::ErrorKind::InvalidData, "invalid saved date"))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parses_java_deadline_format() {
        let task = parse_task("D | 1 | submit report | 2026-09-10T23:59")
            .unwrap()
            .unwrap();
        assert!(task.is_done);
        assert_eq!(task.description, "submit report");
    }

    #[test]
    fn ignores_unknown_records() {
        assert_eq!(parse_task("unknown").unwrap(), None);
    }
}
