use chrono::NaiveDateTime;

/// The data specific to each supported task category.
#[derive(Debug, Clone, PartialEq)]
pub enum TaskKind {
    Todo,
    Deadline {
        by: NaiveDateTime,
    },
    Event {
        from: NaiveDateTime,
        to: NaiveDateTime,
    },
}

/// A task and its completion status.
#[derive(Debug, Clone, PartialEq)]
pub struct Task {
    pub description: String,
    pub is_done: bool,
    pub kind: TaskKind,
}

impl Task {
    pub fn new(description: String, kind: TaskKind) -> Self {
        Self {
            description,
            is_done: false,
            kind,
        }
    }

    pub fn type_icon(&self) -> char {
        match self.kind {
            TaskKind::Todo => 'T',
            TaskKind::Deadline { .. } => 'D',
            TaskKind::Event { .. } => 'E',
        }
    }

    pub fn status_icon(&self) -> char {
        if self.is_done { 'X' } else { ' ' }
    }

    pub fn display_text(&self) -> String {
        match &self.kind {
            TaskKind::Todo => self.description.clone(),
            TaskKind::Deadline { by } => {
                format!("{} (by: {})", self.description, display_date(by))
            }
            TaskKind::Event { from, to } => format!(
                "{} (from: {} to: {})",
                self.description,
                display_date(from),
                display_date(to)
            ),
        }
    }

    pub fn to_save_string(&self) -> String {
        let done = usize::from(self.is_done);
        match &self.kind {
            TaskKind::Todo => format!("T | {done} | {}", self.description),
            TaskKind::Deadline { by } => {
                format!("D | {done} | {} | {}", self.description, save_date(by))
            }
            TaskKind::Event { from, to } => format!(
                "E | {done} | {} | {} -> {}",
                self.description,
                save_date(from),
                save_date(to)
            ),
        }
    }
}

fn display_date(date: &NaiveDateTime) -> String {
    date.format("%b %d %Y, %-I:%M%p").to_string()
}

fn save_date(date: &NaiveDateTime) -> String {
    if date.and_utc().timestamp_subsec_nanos() == 0 && date.second() == 0 {
        date.format("%Y-%m-%dT%H:%M").to_string()
    } else {
        date.format("%Y-%m-%dT%H:%M:%S%.f").to_string()
    }
}

use chrono::Timelike;
