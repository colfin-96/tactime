---
title: Wear Compose instrumentation tests need deterministic dependencies
date: 2026-04-10
category: best-practices
module: TacTime
problem_type: best_practice
component: testing_framework
severity: medium
applies_when:
  - Instrumentation tests need to verify user-visible Wear Compose state changes
  - The screen depends on real time, haptics, or other device-specific services
  - Simplifying flaky tests is starting to remove assertions about actual behavior
tags: [wear-os, compose-ui-test, instrumentation, deterministic-dependencies, haptics]
---

# Wear Compose instrumentation tests need deterministic dependencies

## Context
TacTime’s first pass at Wear OS instrumentation tests became too weak while working around real-device Compose test flakiness. The tests still tapped the `Tell Time` button, but they stopped asserting the visible result, which meant the app could regress its core behavior without the UI suite noticing.

## Guidance
When a Wear Compose screen depends on changing system state like current time or device haptics, keep the screen composable injectable and feed deterministic dependencies into the test version of the UI. That preserves real assertions without depending on live clock values or actual watch vibration hardware.

For TacTime, the stable pattern was:

```kotlin
composeRule.activity.setContent {
    WearApp(
        timeInterpreter = fixedTimeInterpreter("2026-04-10T15:22:00Z"),
        hapticsPlayer = HapticsPlayer { HapticsPlaybackResult.Played },
    )
}

composeRule.onNodeWithTag(TELL_TIME_BUTTON_TAG).performClick()
composeRule.onNodeWithTag(STATUS_TEXT_TAG).assertTextEquals("About 3:15 PM")
```

This keeps the real screen under test while replacing only the volatile inputs.

## Why This Matters
Instrumentation tests that only click controls are easy to keep green, but they stop protecting the actual feature. In TacTime’s case, the important contract is not “the button can be tapped”; it is “tapping the button updates the status text to the interpreted time or fallback copy.” Deterministic dependencies let the test verify that contract directly.

## When to Apply
- A composable reads live time, random values, sensors, or device services during a button press or screen load
- A test workaround is pushing you toward removing user-visible assertions
- The emulator is reliable enough for automation, but the physical watch remains better for manual feel-testing

## Examples
Weak test shape:

```kotlin
composeRule.onNodeWithTag(TELL_TIME_BUTTON_TAG).performClick()
composeRule.onNodeWithTag(TELL_TIME_BUTTON_TAG).performClick()
```

Better test shape:

```kotlin
composeRule.activity.setContent {
    WearApp(
        timeInterpreter = fixedTimeInterpreter("2026-04-10T23:53:00Z"),
        hapticsPlayer = HapticsPlayer {
            HapticsPlaybackResult.Unavailable("No haptics")
        },
    )
}

composeRule.onNodeWithTag(TELL_TIME_BUTTON_TAG).performClick()
composeRule.onNodeWithTag(STATUS_TEXT_TAG)
    .assertTextEquals("About 12:00 AM (no vibration)")
```

## Related
- Related implementation: [MainActivityTest.kt](/Users/colin.finger/tactime/app/src/androidTest/java/com/colfinstudio/tactime/presentation/MainActivityTest.kt)
- Related screen: [MainActivity.kt](/Users/colin.finger/tactime/app/src/main/java/com/colfinstudio/tactime/presentation/MainActivity.kt)
