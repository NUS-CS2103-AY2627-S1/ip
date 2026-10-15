# hermes-mini in Rust

This directory contains a Rust translation of the Java implementation through
Level 9. The Java source in `../src/main/java` remains the course submission.

## Run

Run these commands from the repository root:

```shell
cargo run --manifest-path rust/Cargo.toml
```

By default, the Rust and Java versions both use `data/hermes.txt`. To experiment
without changing the Java application's data, choose another file:

```shell
cargo run --manifest-path rust/Cargo.toml -- \
  --data-file rust/data/hermes.txt
```

## Check the code

```shell
cargo fmt --check --manifest-path rust/Cargo.toml
cargo clippy --manifest-path rust/Cargo.toml --all-targets --all-features -- -D warnings
cargo test --manifest-path rust/Cargo.toml
```

The implementation is split into four modules:

- `app.rs`: interactive loop and command execution
- `command.rs`: parsing user input into typed commands
- `storage.rs`: Java-compatible persistence
- `task.rs`: task data and formatting
