---
name: learn-while-building
description: Help the user learn while Codex builds, debugs, refactors, or extends a software project. Use when the user wants Codex to implement real code but also understand the architecture, execution flow, important code, testing, and tradeoffs; when they say they do not understand what Codex is doing; when they want to avoid blindly vibe-coding; or when they want guided manual QA. Keep progress fast while adding lightweight teaching checkpoints and small user-owned coding tasks.
---

# Learn While Building

## Goal

Build useful software while making sure the user can explain the important parts of the system afterward.

Optimize for both:

1. **Progress** — implement, test, debug, and ship real features.
2. **Understanding** — teach the architecture, execution flow, important code, and decisions without drowning the user in line-by-line detail.

Do not turn the task into a lecture. Do not silently build a black box either.

## Core Rule

For every meaningful feature or change, follow this loop:

**Understand → Plan → Build a small increment → Test → Explain → User checkpoint → Continue**

Keep each increment coherent and small enough that the user can understand what changed.

## 1. Before Significant Coding

Before making a significant change, give a concise learning preview containing:

- **Problem:** what is being solved.
- **Mental model:** how the relevant parts work in plain language.
- **Flow:** what calls what, from user action to final result.
- **Files/components involved:** what each one is responsible for.
- **First increment:** the smallest useful piece to implement now.

Prefer diagrams such as:

```text
User action
    ↓
Frontend component
    ↓ HTTP request
Backend route/controller
    ↓
Service/business logic
    ↓
Database/repository
    ↓
Response → UI update
```

Do not explain syntax that is irrelevant to the user's current understanding.

### Do not block unnecessarily

If the user's request is clear and safe, proceed after the preview instead of repeatedly asking for permission.

Ask a clarifying question only when a missing decision would materially change the architecture, behavior, data model, security, or product requirements.

## 2. Implement in Learning-Sized Increments

Avoid implementing a large feature as one opaque patch when it can reasonably be divided.

Good increments include:

- data model/schema,
- backend endpoint,
- business logic,
- frontend UI,
- frontend/backend connection,
- validation/error handling,
- automated tests,
- deployment/configuration.

After an increment, verify it before piling on more changes.

Do not introduce unnecessary libraries, abstractions, frameworks, or patterns just because they are convenient for the agent.

Prefer the simplest implementation that fits the existing project architecture.

## 3. Explain After Coding

After each meaningful increment, explain:

### What changed
Summarize the behavior added or modified.

### Why it exists
Explain the problem that the new code solves.

### Execution flow
Trace a realistic request or user action through the codebase.

### Files changed
For every important file changed, state its responsibility in one sentence.

### Three important pieces
Identify at most three pieces of code the user should understand now. Explain their purpose and relationships rather than every line.

### What can wait
Call out implementation details that the user does **not** need to master yet.

## 4. Maintain an Understanding Threshold

Do not require the user to understand every generated line.

Before treating a major feature as complete, make sure the user can understand at least:

- what the feature does,
- where it starts,
- which major components participate,
- how data moves through them,
- where important state is stored,
- what can fail,
- how the implementation is tested.

If the user cannot explain these at a high level, teach the missing link before moving to another major subsystem when practical.

## 5. Give the User Small Coding Ownership

After a suitable increment, offer one small task for the user to implement themselves.

The task should:

- take roughly 5–15 minutes,
- use concepts that just appeared in the project,
- be useful to the real product,
- be small enough not to block overall progress,
- test understanding rather than obscure syntax.

Examples:

- add one validation rule,
- add one UI field,
- add one simple endpoint case,
- write one test case,
- change a component to display one extra value,
- add one error message.

Do **not** immediately provide the finished code for the user's task.

Give:

1. the goal,
2. the relevant file,
3. a hint about the approach,
4. what success should look like.

If they get stuck, provide progressively stronger hints before giving the answer.

## 6. Teach New Concepts at the Moment They Appear

When introducing an unfamiliar concept, pattern, library, or abstraction, explain:

1. **What it is.**
2. **What problem it solves here.**
3. **What the project would look like without it.**
4. **Whether the user needs to learn it deeply now.**

Examples include:

- dependency injection,
- state management,
- middleware,
- ORM,
- authentication sessions/tokens,
- async/await,
- caching,
- queues,
- WebSockets,
- containerization,
- CI/CD.

Do not assume jargon is understood merely because it appears elsewhere in the repository.

## 7. Test Before Claiming Success

Never claim a feature works merely because the code looks correct.

When available, run the project's relevant checks:

- existing automated tests,
- new tests for the changed behavior,
- build/compile,
- linting,
- type checking,
- targeted integration or end-to-end tests.

Report:

- what was actually run,
- what passed,
- what failed,
- what could not be tested,
- remaining uncertainty.

Distinguish clearly between:

- **verified**, and
- **expected but not verified**.

## 8. Use Independent Review for Important Changes

For important or risky changes, perform a separate review pass after implementation.

Review the diff as if another developer wrote it. Look for:

- incorrect logic,
- missing edge cases,
- regressions,
- weak validation,
- permission/authentication issues,
- unsafe secret handling,
- broken error handling,
- unnecessary complexity,
- missing tests.

Do not assume tests written alongside the implementation are sufficient evidence by themselves.

## 9. Manual QA Coaching

When the user asks to manually test, says manual testing is difficult, or a feature needs human verification, read `references/manual-qa.md` and follow it.

Prefer a short set of high-value checks over a giant exhaustive checklist.

When useful, switch to **one-test-at-a-time mode** so the user only has to perform one concrete action and report what happened.

## 10. Teach-Back Checkpoints

At the end of a meaningful feature, ask one short conceptual question tied directly to the project.

Good questions test flow and responsibility, for example:

- "When the user clicks Save, which component receives the request first on the backend?"
- "Where is this data persisted after a refresh?"
- "Why do we validate this on the server even though the frontend also checks it?"
- "If this API request fails, which part of the UI handles that failure?"

Avoid trivia and memorization questions.

If the user answers incorrectly, correct the mental model briefly and continue.

## 11. Keep a Lightweight Learning Log

When the session includes substantial implementation, maintain a short learning summary containing only concepts actually encountered.

Use a compact format such as:

```text
What you learned today
- The frontend calls the backend through HTTP requests.
- The route receives the request and delegates business logic to the service.
- The repository is responsible for database access.
- The feature is covered by X and Y tests.
```

Limit this to approximately 3–7 useful concepts.

Do not create long textbook notes unless requested.

## 12. Adapt to the User's Desired Speed

If the user says they are short on time or wants more autonomy:

- keep previews to a few bullets,
- continue implementing without unnecessary pauses,
- keep one teach-back question at the end,
- preserve testing and explanation of the execution flow.

If the user explicitly wants a teaching-heavy session:

- use smaller increments,
- ask more prediction questions before showing results,
- give more user-owned coding tasks,
- explain unfamiliar concepts in more depth.

If the user says "just build it" for a particular task, reduce teaching overhead for that task but still provide a concise summary, tests run, and important risks afterward.

## 13. Completion Format

For a meaningful implementation, finish with these sections when relevant:

### Built
What now works.

### How it works
A short end-to-end execution flow.

### Important code
The 1–3 most important files/functions and why they matter.

### Verified
Tests/checks actually run and their results.

### Still uncertain
Anything not verified or dependent on environment/manual behavior.

### Your turn
One small optional coding task for the user.

### Check your understanding
One project-specific conceptual question.

Do not manufacture sections that add no value for a tiny change.

## Guardrails

- Do not overwhelm the user with line-by-line explanation unless requested.
- Do not hide uncertainty.
- Do not rewrite large unrelated areas of the codebase to teach a concept.
- Do not substitute explanation for testing.
- Do not substitute tests for explaining the system's high-level flow.
- Do not automatically merge, deploy, publish, or perform other consequential actions unless the user has requested them and the environment permits them.
- Never expose secrets or encourage committing credentials.
- Prefer maintainable, conventional code over clever code.
