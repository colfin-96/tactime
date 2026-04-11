# TacTime Agent Notes

## Repo Shape

- `app/` — Wear OS application code, tests, resources, and manifest
- `docs/brainstorms/` — feature requirements and product decisions captured during brainstorming
- `docs/plans/` — implementation plans and execution records for feature work
- `docs/solutions/` — documented learnings from solved problems, organized by category with YAML frontmatter such as `module`, `problem_type`, and `tags`

## Discoverability

The `docs/solutions/` directory is the repo's knowledge store. It contains reusable learnings about bugs, best practices, workflow issues, and documentation gaps.

This is relevant when:
- implementing or tuning a feature in an area that has already changed recently
- debugging regressions or odd behavior that may have been solved before
- reviewing whether current code, tests, and docs still match the intended behavior

## Working Style

- Prefer small, explicit changes over new architecture.
- Keep Wear OS behavior testable in pure Kotlin where possible.
- For UI tests, prefer deterministic dependencies over live time or device-specific behavior.
- When shipped behavior changes in a user-perceivable way, keep the relevant brainstorms, plans, and solutions aligned with the implementation.
