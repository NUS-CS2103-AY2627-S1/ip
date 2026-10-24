# Week 7 Optional Features Design

## Goal

Add an optional AI help workflow and two résumé-oriented improvements to Kiki
without making the application unusable when no AI API key is configured.

## AI Help Feature

Kiki will accept `@ai <question>` in both the CLI and GUI. The command will
answer read-only questions about Kiki's supported commands and will not create,
edit, or delete tasks. The LLM integration will use LangChain4j with a
provider-compatible endpoint configured through `LLM_API_KEY` and optional
environment variables for the endpoint and model.

When `LLM_API_KEY` is absent, Kiki will return a clear local setup message
instead of failing. Network errors and malformed responses will also become
friendly Kiki error messages. The key will never be stored in source code,
tests, or the repository.

The AI client will be isolated behind a small service interface so parser,
task, and GUI tests do not need network access. Unit tests will cover command
recognition and unavailable-service behavior; live API calls will not be part
of automated tests.

## Résumé Improvement 1: GUI Presentation

Refine the existing JavaFX layout for clearer conversation hierarchy,
consistent spacing, and better behavior when messages become long. The change
will preserve the current command flow and remain usable at the existing
window size.

## Résumé Improvement 2: Task Priorities

Add an optional priority to tasks using a small, explicit command syntax and
persist it in storage. The list view will display the priority, and sorting
will use priority only when the user explicitly requests priority sorting.
Existing task files without priority data will continue to load with a default
priority.

## Verification

Run the Java 25 Gradle test suite, add focused tests for parsing, persistence,
and priority behavior, and manually exercise `@ai` without an API key through
the GUI and CLI.
