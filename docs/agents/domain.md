# Domain Docs

This repository uses a single-context domain-documentation layout.

## Before exploring

Read these files when they exist:

- `CONTEXT.md` at the repository root
- Relevant ADRs under `docs/adr/`

If they do not exist, proceed silently. The domain-modeling skill creates them
when terminology or architectural decisions need to be recorded.

## Layout

```text
/
├── CONTEXT.md
├── docs/
│   └── adr/
└── src/
```

## Vocabulary

Use domain terms as defined in `CONTEXT.md`. Avoid introducing synonyms that
the glossary explicitly rejects.

If a necessary concept is missing, reconsider whether it is established
project language or record the gap for domain modeling.

## ADR conflicts

If proposed work contradicts an existing ADR, identify the conflict explicitly
rather than silently overriding the decision.
