---
name: appium-test-design
description: Design, write, refactor, and review Appium mobile test automation (Android + iOS) using SOLID, DRY, and YAGNI with Fluent Screen Actions instead of the Page Object Model. Covers thread-safe parallel execution, a cross-platform driver factory, stable locators, explicit waits, W3C gesture helpers, capability externalization, and dependency hygiene. Use when writing or structuring mobile UI tests, reviewing Appium/Selenium code, replacing page objects, or setting up parallel or cross-platform runs.
---

# Appium Test Design

This skill's full, authoritative guidance lives in **`AGENTS.md` at the
repository root** (relative path from here: `../../../AGENTS.md`). It is the
single source of truth, shared across Claude Code, Cline, and Roo Code.

**When this skill triggers, do this:**

1. **Read `AGENTS.md` at the repository root** and apply every rule in it.
2. Hold to the headline rules even before reading the file in full:
   - **No Page Object Model** - use **Fluent Screen Actions** (four one-way
     layers: test → screen actions → interactions → driver; locators are data).
   - **Explicit waits only** (no `implicitlyWait`, no `Thread.sleep`).
   - **Type to `AppiumDriver`**, build it via a `DriverFactory`, hold it in a
     `ThreadLocal` - so the same tests run on Android + iOS and in parallel.
   - Locators as `By` constants (`accessibilityId` first), verified against the
     running app; assertions live in tests, not screens; externalize capabilities;
     don't declare Selenium directly, but pin its transitive version (Selenium BOM,
     currently <= 4.33.0, or the java-client crashes at runtime).
   - Tests follow **F.I.R.S.T.** (Fast, Isolated, Repeatable, Self-verifying,
     Timely) and read as Build-Operate-Check; treat test code as production code.

Keep edits to the guidance in `AGENTS.md`, not here - this file is just the
Claude Code entry point to it.
