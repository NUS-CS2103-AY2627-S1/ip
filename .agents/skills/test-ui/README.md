# test-ui Skill

Automated UI/IO testing suite and runner for the Braun chatbot application.

## Files
- `SKILL.md`: Main instructions and documentation for agent and developer usage.
- `scripts/run-ui-tests.py`: Python test runner engine (uses Python 3 standard library).

## Usage
```bash
# Run default test plan
python3 .agents/skills/test-ui/scripts/run-ui-tests.py

# Run custom test plan
python3 .agents/skills/test-ui/scripts/run-ui-tests.py --plan test/ui-test-plan.md
```
