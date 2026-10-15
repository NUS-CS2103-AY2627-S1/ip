use std::io::{self, BufRead};
use std::path::PathBuf;

use crate::command::{self, Command};
use crate::storage::Storage;
use crate::task::{Task, TaskKind};

const BANNER: &str = r"      _                                                   _       _
     | |__   ___ _ __ _ __ ___   ___  ___       _ __ ___ (_)_ __ (_)
     | '_ \ / _ \ '__| '_ ` _ \ / _ \/ __|_____| '_ ` _ \| | '_ \| |
     | | | |  __/ |  | | | | | |  __/\__ \_____| | | | | | | | | | |
     |_| |_|\___|_|  |_| |_| |_|\___||___/     |_| |_| |_|_|_| |_|_|";
const INDENT: &str = "     ";
const DIVIDER: &str = "    ____________________________________________________________";
const MAX_TASKS: usize = 100;

/// Owns the task list and executes commands from standard input.
pub struct App {
    storage: Storage,
    tasks: Vec<Task>,
}

impl App {
    pub fn new(data_file: PathBuf) -> Self {
        Self {
            storage: Storage::new(data_file),
            tasks: Vec::new(),
        }
    }

    pub fn run(mut self) {
        print_greeting();
        self.tasks = match self.storage.load() {
            Ok(tasks) => tasks,
            Err(_) => {
                print_error("I couldn't load the saved tasks.");
                Vec::new()
            }
        };
        for line in io::stdin().lock().lines() {
            let Ok(line) = line else { break };
            match command::parse(&line) {
                Ok(Command::Bye) => {
                    print_message("Bye. Hope to see you again soon!");
                    break;
                }
                Ok(command) => self.execute(command),
                Err(error) => print_error(error.message()),
            }
        }
    }

    fn execute(&mut self, command: Command) {
        match command {
            Command::Bye => unreachable!("bye is handled by the application loop"),
            Command::List => self.print_task_list(),
            Command::Mark { number, done } => self.mark_task(number, done),
            Command::Delete { number } => self.delete_task(number),
            Command::Find { keyword } => self.find_tasks(&keyword),
            Command::Todo { description } => self.add_task(Task::new(description, TaskKind::Todo)),
            Command::Deadline { description, by } => {
                self.add_task(Task::new(description, TaskKind::Deadline { by }));
            }
            Command::Event {
                description,
                from,
                to,
            } => self.add_task(Task::new(description, TaskKind::Event { from, to })),
        }
    }

    fn print_task_list(&self) {
        println!("{DIVIDER}");
        println!("{INDENT}Here are the tasks in your list:");
        for (index, task) in self.tasks.iter().enumerate() {
            println!(
                "{INDENT}{}.[{}][{}] {}",
                index + 1,
                task.type_icon(),
                task.status_icon(),
                task.display_text()
            );
        }
        println!("{DIVIDER}");
    }

    fn find_tasks(&self, keyword: &str) {
        println!("{DIVIDER}");
        println!("{INDENT}Here are the matching tasks:");
        let keyword = keyword.to_lowercase();
        for (index, task) in self.tasks.iter().enumerate() {
            if task.description.to_lowercase().contains(&keyword) {
                println!(
                    "{INDENT}{}.[{}][{}] {}",
                    index + 1,
                    task.type_icon(),
                    task.status_icon(),
                    task.display_text()
                );
            }
        }
        println!("{DIVIDER}");
    }

    fn mark_task(&mut self, number: usize, done: bool) {
        let Some(task) = number
            .checked_sub(1)
            .and_then(|index| self.tasks.get_mut(index))
        else {
            print_error("That task number is not in your list.");
            return;
        };
        task.is_done = done;
        if done {
            println!("{DIVIDER}");
            println!("{INDENT}Nice! I've marked this task as done:");
            println!("{INDENT}  [X] {}", task.description);
            println!("{DIVIDER}");
        } else {
            println!("{DIVIDER}");
            println!("{INDENT}OK, I've marked this task as not done yet:");
            println!("{INDENT}  [ ] {}", task.description);
            println!("{DIVIDER}");
        }
        self.save();
    }

    fn delete_task(&mut self, number: usize) {
        let Some(index) = number
            .checked_sub(1)
            .filter(|index| *index < self.tasks.len())
        else {
            print_error("That task number is not in your list.");
            return;
        };
        let task = self.tasks.remove(index);
        self.save();
        println!("{DIVIDER}");
        println!("{INDENT}Noted. I've removed this task:");
        println!(
            "{INDENT}  [{}][{}] {}",
            task.type_icon(),
            task.status_icon(),
            task.display_text()
        );
        println!(
            "{INDENT}Now you have {} tasks in the list.",
            self.tasks.len()
        );
        println!("{DIVIDER}");
    }

    fn add_task(&mut self, task: Task) {
        if self.tasks.len() >= MAX_TASKS {
            print_error("Your task list is full.");
            return;
        }
        println!("{DIVIDER}");
        println!("{INDENT}Got it. I've added this task:");
        println!(
            "{INDENT}  [{}][ ] {}",
            task.type_icon(),
            task.display_text()
        );
        self.tasks.push(task);
        self.save();
        println!();
        println!(
            "{INDENT}Now you have {} tasks in the list.",
            self.tasks.len()
        );
        println!("{DIVIDER}");
    }

    fn save(&self) {
        if self.storage.save(&self.tasks).is_err() {
            print_error("I couldn't save the tasks.");
        }
    }
}

fn print_greeting() {
    println!("{DIVIDER}");
    println!("{BANNER}\n");
    println!("{INDENT}Hello! I'm hermes-mini");
    println!("{INDENT}What can I do for you?");
    println!("{DIVIDER}");
}

fn print_message(message: &str) {
    println!("{DIVIDER}");
    println!("{INDENT}{message}");
    println!("{DIVIDER}");
}

fn print_error(message: &str) {
    println!("{DIVIDER}");
    println!("{INDENT}OOPS!!! {message}");
    println!("{DIVIDER}");
}
