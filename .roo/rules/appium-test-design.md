# Appium Test Design

Follow the team's Appium automation standard in **`AGENTS.md` at the repository
root**. It is the single source of truth (Roo Code reads `AGENTS.md` natively
unless `roo-cline.useAgentRules` is false; this rule guarantees it loads either
way). Apply all of it.

Non-negotiable headlines:

- **No Page Object Model.** Use **Fluent Screen Actions** - four one-way layers:
  test → screen actions → interactions → driver. Locators are data, not behavior.
- **Explicit waits only.** Never set `implicitlyWait`; never `Thread.sleep`.
- **Type to `AppiumDriver`**, build it via a `DriverFactory`, hold it in a
  `ThreadLocal` - one codebase runs on Android + iOS and in parallel.
- Locators as `By` constants (`accessibilityId` first), verified against the
  running app (not guessed from a stale dump). Assertions live in tests, not
  screens. Externalize capabilities. Don't declare Selenium directly, but pin its
  transitive version with the Selenium BOM (currently <= 4.33.0); the java-client's
  open range otherwise resolves to a Selenium that crashes it at runtime.
- Tests follow **F.I.R.S.T.** (Fast, Isolated, Repeatable, Self-verifying,
  Timely) and read as Build-Operate-Check; test code is production code.

Full rules, tables, and a worked example: read `AGENTS.md`.
