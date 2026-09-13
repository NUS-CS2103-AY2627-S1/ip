# ADR 0003: Add Athena's Archive

## Status

Accepted

## Context

Odysseus needs to help a traveler memorize Odyssey-related vocabulary and
answers, while preserving the existing task log and conversational interface.

## Decision

Add a user-authored `LearningDeck` of prompt-and-answer `LearningCard` values.
Keep its local persistence in a versioned `odysseus-learning.txt` sidecar file
whose text fields are Base64-encoded. Select the next card by lowest mastery,
then fewest reviews, then insertion order. Keep only the active study turn in
memory.

## Consequences

* **Benefit:** Users can study course-specific vocabulary, facts, and themes
  without choosing or licensing a bundled translation or question bank.
* **Benefit:** Existing `odysseus.txt` task logs remain readable and unchanged.
* **Tradeoff:** Automatic answer checking is normalized exact matching; users
  use `reveal`, then `mastered` or `again`, for valid alternative answers.
* **Follow-up:** Add time-based spaced repetition only after the core study
  loop proves useful.
