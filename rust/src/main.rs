mod app;
mod command;
mod storage;
mod task;

use std::path::PathBuf;

use clap::Parser;

use crate::app::App;

/// A small command-line task manager.
#[derive(Debug, Parser)]
#[command(version, about)]
struct Cli {
    /// File used to persist tasks between runs.
    #[arg(long, default_value = "data/hermes.txt")]
    data_file: PathBuf,
}

fn main() {
    let cli = Cli::parse();
    App::new(cli.data_file).run();
}
