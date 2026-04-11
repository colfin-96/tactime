---
title: Keep TacTime haptics docs aligned with shipped behavior
date: 2026-04-11
category: documentation-gaps
module: TacTime
problem_type: documentation_gap
component: documentation
severity: medium
applies_when:
  - Haptic behavior is being tuned after the initial brainstorm and plan docs already exist
  - A change preserves the feature concept but still changes the actual user-perceived pattern
  - Review findings point out that the requirements or plan no longer match the implementation
tags: [tactime, haptics, documentation-drift, compound-engineering, wear-os]
---

# Keep TacTime haptics docs aligned with shipped behavior

## Context
TacTime's haptics tuning started as a straightforward timing pass, then evolved on-device into a slightly different shipped behavior: the vibration pattern gained a lead-in cue before the countable hour and quarter pulses. The code and tests were updated quickly, but the Compound Engineering brainstorm and follow-up plan still described the old pulse language and the older timing profile.

## Guidance
When a tuning pass changes the actual user-perceived pattern, update the requirements and plan docs in the same slice of work rather than treating documentation as optional cleanup. Even when the feature concept stays the same, the documented behavior needs to reflect what the watch now does.

For TacTime, the implementation became:

```kotlin
val Balanced = PulseTimingProfile(
    leadInPulseDurationMs = 320L,
    leadInPauseDurationMs = 320L,
    pulseDurationMs = 140L,
    pulseGapDurationMs = 110L,
    groupPauseDurationMs = 440L,
)
```

That meant the docs also needed to change from describing `3:00` as just `3 pulses` to describing it as a lead-in cue followed by `3 pulses`, and the tuning plan needed to record the final validated timing values and the fact that watch-side validation had completed.

A good practical rule is:

1. Change the code and tests
2. Re-read the brainstorm/requirements doc for language drift
3. Re-read the active plan for stale timing values, incomplete checkboxes, or outdated validation notes
4. Update both before considering the tuning pass done

## Why This Matters
Compound Engineering docs are decision artifacts, not ornamentation. Future review, planning, and implementation work will trust those docs when deciding whether behavior is correct. If the code says one thing and the source-of-truth docs say another, the team will waste time debating the wrong behavior, reviewing against stale requirements, or writing tests for a pattern the app no longer ships.

In TacTime's case, the lead-in cue was intentionally accepted after wrist testing. Leaving the old wording in place would have made the shipped behavior look like a regression when it was actually the approved outcome.

## When to Apply
- A tuning pass changes timing, sequencing, or signaling that a user can perceive directly
- A review finds documentation drift between implementation and CE artifacts
- Manual device validation changes the preferred solution after the original plan was written
- A feature keeps the same goal but the final interaction details evolve during iteration

## Examples
Stale requirement wording:

```markdown
- R7. The haptic pattern must encode the hour as a first group of short pulses.
- R10. `3:00` -> `3 pulses`
```

Aligned requirement wording:

```markdown
- R7. The haptic pattern may begin with a distinct lead-in pulse and short settling pause.
- R8. The hour is the first countable group of short pulses.
- R11. `3:00` -> lead-in cue, `3 pulses`
```

Stale plan completion note:

```markdown
- Completed with ... `140ms` pulse, `110ms` intra-group gap, `440ms` group pause.
- [ ] Unit 3: Validate and finalize the balanced timing on the Galaxy Watch
```

Aligned plan completion note:

```markdown
- Completed with ... `320ms` lead-in pulse, `320ms` lead-in pause, `140ms` pulse, `110ms` intra-group gap, `440ms` group pause.
- [x] Unit 3: Validate and finalize the balanced timing on the Galaxy Watch
```

## Related
- Related requirements: [2026-04-10-tactime-mvp-requirements.md](/Users/colin.finger/tactime/docs/brainstorms/2026-04-10-tactime-mvp-requirements.md)
- Related plan: [2026-04-11-002-tune-tactime-haptics.md](/Users/colin.finger/tactime/docs/plans/2026-04-11-002-tune-tactime-haptics.md)
- Related implementation: [PulsePlan.kt](/Users/colin.finger/tactime/app/src/main/java/com/colfinstudio/tactime/haptics/PulsePlan.kt)
- Related test learning: [wear-compose-instrumentation-tests-need-deterministic-dependencies-2026-04-10.md](/Users/colin.finger/tactime/docs/solutions/best-practices/wear-compose-instrumentation-tests-need-deterministic-dependencies-2026-04-10.md)
