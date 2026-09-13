# Project Brief

## Product Goal

Build a **Personal Assistant Chatbot** for an individual user so they can keep
track of various things through a conversational interface.

## Primary Workflows

1. **Keep track of information**: the user can record and review the things
   they want the assistant to remember.

Further product workflows have not been specified yet. Confirm them before
implementing application behavior.

## Non-Goals

* Product requirements beyond the stated personal-assistant purpose.
* External service integrations, until a requirement explicitly introduces one.

## External Systems

| System | Why it exists | Interface owner | Failure fallback |
| --- | --- | --- | --- |
| None specified | n/a | n/a | n/a |

## Definition Of Done

A feature is complete only when it has all of the following:

1. A small design artifact update (`design-tree.md` and/or ADR) when design changes.
2. Clear boundary types/interfaces (where language supports this).
3. Behavior tests plus at least one edge case test.
4. Deterministic checks run (`./.beryl/scripts/check.sh` and relevant project checks).
5. No new illegal boundary crossings.
