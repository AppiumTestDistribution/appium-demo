# Appium Test Automation - engineering standard

> **Single source of truth.** This file is read natively by Cline and Roo Code,
> and loaded into Claude Code via `.claude/skills/appium-test-design/`. Edit the
> guidance **here**; the per-tool adapters just point at this file.

This is the standard for writing, structuring, and reviewing Appium mobile UI
automation (java-client 9.x + JUnit 5, Android **and** iOS). It is intentionally
**generic** - apply it to any app under test. Examples use placeholder names
like `com.example.app`, `LOGIN_BUTTON`, and `SEARCH_FIELD`.

---

## TL;DR - the rules

1. **No Page Object Model.** Use **Fluent Screen Actions**: four small layers,
   each with one job (see [Architecture](#architecture)).
2. **Locators are data, not behavior.** Plain `By` constants kept apart from the
   code that uses them. Prefer `accessibilityId` - it works on Android *and* iOS.
3. **Screen classes expose intentions, never mechanics or assertions.**
   `loginAs(user, pass)` - not `clickLoginButton()` then `enterUsername()`.
4. **Explicit waits only.** Never set `implicitlyWait`. Never `Thread.sleep`.
5. **One driver per thread** via `ThreadLocal`, built by a `DriverFactory` that
   branches on platform. Program to `AppiumDriver`, never `AndroidDriver`.
6. **Externalize capabilities.** No device names, app ids, or server URLs
   hardcoded in test code - load them from config / system properties.
7. **Tests own the assertions** and read like a user story.
8. **Don't declare Selenium as a dependency, but DO pin its transitive version.**
   The java-client pulls Selenium in transitively via an open range that resolves
   to a build which breaks it; constrain it with the Selenium BOM (see
   [Dependency hygiene](#dependency-hygiene)).
9. **DRY on the third repetition, not the first** (YAGNI). Don't build a
   framework before you have tests that need one.

---

## Why NOT the Page Object Model

POM was designed for stable desktop web. On mobile it rots:

| POM smell | Why it hurts | Fluent Screen Actions answer |
|---|---|---|
| One class = locators **+** actions **+** waits **+** assertions **+** navigation | Violates **S**RP; changes for five reasons; becomes a god object | Four layers, each with one reason to change |
| `BasePage` inheritance tree | Fragile base class; forces unused methods on subclasses | Composition over inheritance - share via small helpers |
| `getUsernameField()` / `clickLoginButton()` getters | Leaks mechanics; tests read like UI plumbing | Screens expose **intentions** and hide elements |
| Assertions inside page methods | Mixes query and command; action not reusable without its assertion | Assertions live in the **test**; screens only *do* |
| Page bound to `AndroidDriver` | Can't reuse for iOS; downcasting everywhere | Bind to `AppiumDriver`; one codebase, two platforms |
| Locators as fields on the page | Selectors and behavior churn the same file | Locators are separate `By` constants - data, not code |

**"Aren't Screen Actions just renamed Page Objects?"** No. A Page Object *owns*
its elements and often its assertions. A Screen Action class owns **neither** -
locators are external data, assertions are in the test, and the only public
methods are user intentions that delegate to shared primitives. It's the
difference between a 300-line `LoginPage` and a 15-line `LoginScreen`.

---

## Architecture

Fluent Screen Actions = four layers; dependencies point **one way only**:

```
test            arranges data, calls intentions, ASSERTS
  │   (depends on intentions, not the driver)
screen actions  intention methods: loginAs() -> HomeScreen, search() -> ResultsScreen
  │   (delegate to primitives; hold NO assertions, NO element getters)
interactions    tap / type / waitVisible (explicit waits)  +  gestures (W3C Actions)
  │
locators        By / Target constants - pure data, no logic
driver          DriverFactory (per platform)  +  Drivers (ThreadLocal holder)
config          platform, server URL, capabilities - from files/env
```

Nothing points back up. That single rule is what keeps the suite easy to reason
about. See the [worked example](#worked-example) for full code.

---

## SOLID, applied to Appium

- **S - Single Responsibility:** locators, actions, assertions, driver lifecycle,
  and config are five different files, not one `BasePage`.
- **O - Open/Closed:** add a gesture by adding a method to `Gestures`, not by
  editing every screen. Add a platform by adding a branch to `DriverFactory`.
- **L - Liskov:** tests hold an `AppiumDriver`; an `IOSDriver` works anywhere an
  `AndroidDriver` does because nothing downcasts.
- **I - Interface Segregation:** a screen exposes only the actions a test needs;
  no fat base class forces unused methods.
- **D - Dependency Inversion:** tests depend on the `DriverFactory` abstraction
  and intention methods - never on `new AndroidDriver(...)`.

## DRY without over-engineering (YAGNI)

- **DRY the things that genuinely repeat:** capabilities (one config source),
  interaction primitives (one `tap`/`type`/`wait`), gesture math (one `swipe`),
  test-data builders.
- **Rule of three:** extract a helper on the *third* duplication, not the first.
- **Helpers stay DRY; test bodies stay readable (DAMP).** Keep production and
  helper code DRY, but a *test method* favors Descriptive And Meaningful Phrases
  over strict de-duplication. Don't bury a test's intent under shared setup.
  Readability is the first priority in tests (R. C. Martin, *Clean Code*).
- **YAGNI:** no custom reporting, plugin systems, DSLs, or speculative wrapper
  layers before a test needs them. Don't wrap every Appium call "just in case."
- **The one seam worth pre-building:** cross-platform support (`DriverFactory` +
  `accessibilityId` locators + `AppiumDriver` typing). Nearly free now, expensive
  to retrofit. Build the seam; don't build the iOS suite until you ship iOS.

---

## Locators

- **Prefer `AppiumBy.accessibilityId(...)`** - maps to `content-desc` on Android
  and `accessibilityIdentifier` on iOS, so the *same* locator works on both and
  it's the most stable selector.
- **Avoid XPath** (slow, brittle) and **index-based** selectors.
- **Locators are data.** Keep them in a `Screens`/`Locators` holder as `By`
  constants, separate from action code:

  ```java
  // AVOID: locator welded to a driver-bound page object
  private final AppiumBy loginBtn = (AppiumBy) AppiumBy.accessibilityId("login_button");

  // PREFER: locator as plain, platform-neutral data
  public static final By LOGIN_BUTTON = AppiumBy.accessibilityId("login_button");
  ```
- When platforms truly diverge, hide it behind **one** method (see
  [Cross-platform](#parallel-execution--cross-platform)).
- **Verify every locator against the running app**, never from memory or a stale
  UI dump. A guessed `accessibilityId` that actually lives on a *different* screen
  is a classic trap: the code compiles, then times out at runtime. Inspect the
  live hierarchy (Appium Inspector or a fresh `uiautomator dump`) before trusting
  an id, and confirm the *screen order* (which screen the app opens on) the same way.

## Waits

- **Explicit waits only:**

  ```java
  new WebDriverWait(driver, Duration.ofSeconds(15))
      .until(ExpectedConditions.elementToBeClickable(Screens.LOGIN_BUTTON))
      .click();
  ```
- **Never set `implicitlyWait`.** Mixing implicit + explicit waits compounds them
  into unpredictable timeouts. Pick explicit - it's per-condition and intent-revealing.
- **Never `Thread.sleep`.** Wait on a *condition*, not a clock.

## Gestures

- Use the **W3C Actions API** (`PointerInput` / `Sequence`) and **wrap it once**
  in a `Gestures` helper so no test repeats pointer math (DRY).
- The legacy `TouchAction` API is **removed** in java-client 9 - don't copy it
  from old snippets.
- For common gestures, driver-side **`mobile:` commands** are simpler and
  platform-tuned: `driver.executeScript("mobile: swipeGesture", Map.of(...))`
  (Android: `swipeGesture`/`dragGesture`; iOS: `mobile: swipe`/`dragFromToForDuration`).
  W3C Actions stay the portable fallback.

---

## Parallel execution & cross-platform

Both hinge on the same two pieces: a **`DriverFactory`** that builds the right
driver from config, and a **`ThreadLocal<AppiumDriver>`** so each test thread
owns its own session.

```java
// DriverFactory: the ONLY place a concrete driver is built
public static AppiumDriver create(TestConfig cfg) {
    switch (cfg.platform()) {
        case ANDROID: return new AndroidDriver(cfg.serverUrl(), new UiAutomator2Options()
                .setAutomationName("UiAutomator2")
                .setDeviceName(cfg.deviceName())
                .setApp(cfg.appPath()));          // or setAppPackage/setAppActivity
        case IOS:     return new IOSDriver(cfg.serverUrl(), new XCUITestOptions()
                .setAutomationName("XCUITest")
                .setDeviceName(cfg.deviceName())
                .setPlatformVersion(cfg.platformVersion())
                .setBundleId(cfg.bundleId()));
        default: throw new IllegalStateException("Unsupported platform");
    }
}
```

```java
// Drivers: one session per thread - required for safe parallel runs
public final class Drivers {
    private static final ThreadLocal<AppiumDriver> TL = new ThreadLocal<>();
    public static void set(AppiumDriver d) { TL.set(d); }
    public static AppiumDriver get() { return TL.get(); }
    public static void unset() {                 // quit + remove to avoid leaks
        AppiumDriver d = TL.get();
        if (d != null) d.quit();
        TL.remove();
    }
}
```

```properties
# src/test/resources/junit-platform.properties - JUnit 5 native parallelism
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.mode.classes.default = concurrent
junit.jupiter.execution.parallel.config.strategy = fixed
junit.jupiter.execution.parallel.config.fixed.parallelism = 4   # match device count
```

Rules:
- **Parallelism must match available sessions** - 4 threads need 4
  emulators/simulators/real devices (or cloud slots).
- **Tests must be independent** (no shared state, no ordering). Parallelism only
  amplifies hidden ordering bugs.
- **Cross-platform locators when they truly diverge** - isolate the fork in one
  method, never sprinkle `if (platform == IOS)` across the suite:

  ```java
  public static By menu(Platform p) {
      return p == Platform.IOS
          ? AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND name == 'Menu'")
          : AppiumBy.androidUIAutomator("new UiSelector().description(\"Menu\")");
  }
  ```
- **Scaling out:** `ThreadLocal` + factory scales to whatever infrastructure
  offers. Point the Appium server URL at a Selenium Grid / Appium relay or a cloud
  device farm (BrowserStack / Sauce Labs / LambdaTest) and add vendor caps in the
  factory - **no test code changes.**

---

## Configuration & capabilities

Nothing about *where* or *what* you run belongs in test code. Externalize:

- Per-platform capability files (`config/android.json`, `config/ios.json`) or a
  `TestConfig` loaded from system properties / environment variables.
- Select platform at runtime: `mvn test -Dplatform=ios`.
- This is what lets the *same* test target a local emulator, CI, or a cloud
  device farm without edits.

---

## Test design

- **Arrange-Act-Assert.** A test reads like a sentence.
- **Assertions live in the test**, not in screen actions. Screens *do*; tests *check*.
- **Independent & idempotent.** No test depends on another's order or side effects.
- **Reset state deliberately.** `noReset(true)` is fast but leaks state between
  tests; prefer a known clean start, or reset the relevant data explicitly.
- **Behavioral names:** `loginThenHomeScreenLoads()`, not `test1()`.
- **One concept per test.** Multiple asserts are fine if they verify one behavior.

---

## Clean tests (craftsmanship)

Treat test code as production code, and apply Robert C. Martin's test rules.

**F.I.R.S.T., translated to Appium:**

- **Fast:** UI tests are inherently slow, so keep them few and focused, run them
  in parallel, and reuse expensive setup where it is safe. Push business-logic
  checks down to faster unit/integration tests (the test pyramid); do not verify
  everything through the UI.
- **Isolated:** each test arranges its own state and owns its own driver session
  (`ThreadLocal`). No shared mutable state; runnable alone and in any order.
- **Repeatable:** deterministic on any machine. Explicit waits (never
  `Thread.sleep`), controlled test data, a known clean start, and no dependence
  on yesterday's data or a flaky network.
- **Self-verifying:** the test decides pass/fail in code. Never judge a result by
  eyeballing a screenshot or scanning logs.
- **Timely:** write the automation with the feature, not months later.

**Test code is production code.** Same naming, small methods, refactoring, and
review. Dirty tests rot until the team stops trusting them, then stops running
them, and the safety net is gone. When you build the framework's own helpers,
drive them with tests (the Three Laws of TDD).

**A test is a domain language (Build-Operate-Check).** Fluent Screen Actions *is*
the testing DSL Martin advocates: intention methods are the vocabulary, and every
test reads as Build (arrange via screen actions), Operate (the action under
test), Check (assert). If a test does not read like a sentence in your app's
domain, the screen API is still too low-level.

**Humble Object at the boundary.** The Appium/driver layer is the slow,
hard-to-test boundary, so keep it thin and dumb (`Interactions`, `Gestures`).
Keep decisions, data shaping, and assertions out of it, so the logic that matters
lives in fast, plain, testable code.

---

## Dependency hygiene

**Never declare Selenium as a direct dependency.** The Appium java-client pulls the
Selenium modules it needs (`selenium-api`, `selenium-remote-driver`,
`selenium-support`) transitively. Adding `selenium-java` (or any `selenium-*`
artifact) to `<dependencies>` only invites conflicts.

**But you must constrain the transitive Selenium version.** java-client 9.x (up to
9.4.0) declares those modules with an **open range** `[4.19.0, 5.0)`, and Maven
resolves a range to the **newest** available version. Today that is Selenium
4.45.x, and **Selenium 4.34.0 removed `org.openqa.selenium.ContextAware`** (and the
old `html5.LocationContext`), which the java-client bytecode still references.
Depending on the java-client version this surfaces as a **compile** error or, more
treacherously, only at **runtime** when the driver is created (the project compiles
fine, then blows up in `@BeforeEach`):

```
java.lang.NoClassDefFoundError: org/openqa/selenium/ContextAware
    at BaseTest.setUp(...)   // new AndroidDriver(...)
```

**Fix: pin the transitive Selenium with the Selenium BOM** (this is version
management, not a new dependency). 4.33.0 is the last release that still contains
`ContextAware`:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-bom</artifactId>
      <version>4.33.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

This forces every `selenium-*` module to one compatible version without adding
Selenium to `<dependencies>`. Bumping java-client does **not** remove the need for
it: even the latest java-client (9.4.0) predates Selenium 4.34 and still references
`ContextAware`. Revisit the pin once a java-client ships built against Selenium
4.34+, then you can raise or drop it. Run `mvn dependency:tree` to confirm what
resolved.

---

## Worked example

A generic login → search flow in Fluent Screen Actions. Adapt names to your app.

```java
// 1. LOCATORS - pure data
public final class Screens {
    private Screens() {}
    public static final By USERNAME     = AppiumBy.accessibilityId("username_field");
    public static final By PASSWORD     = AppiumBy.accessibilityId("password_field");
    public static final By LOGIN_BUTTON = AppiumBy.accessibilityId("login_button");
    public static final By SEARCH_FIELD = AppiumBy.accessibilityId("search_field");
    public static final By RESULT_ROW   = AppiumBy.accessibilityId("result_row");
}
```

```java
// 2. INTERACTION PRIMITIVES - explicit waits, platform-agnostic
public final class Interactions {
    private static final Duration TIMEOUT = Duration.ofSeconds(15);
    private final AppiumDriver driver;
    public Interactions(AppiumDriver driver) { this.driver = driver; }

    public WebElement waitVisible(By by) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(by));
    }
    public void tap(By by) {
        new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(by)).click();
    }
    public void type(By by, String text) { waitVisible(by).sendKeys(text); }
}
```

```java
// 3. SCREEN ACTIONS - intentions only; no assertions, no element getters
public final class LoginScreen {
    private final AppiumDriver driver;
    private final Interactions ui;
    public LoginScreen(AppiumDriver driver) { this.driver = driver; this.ui = new Interactions(driver); }

    public HomeScreen loginAs(String user, String pass) {
        ui.type(Screens.USERNAME, user);
        ui.type(Screens.PASSWORD, pass);
        ui.tap(Screens.LOGIN_BUTTON);
        return new HomeScreen(driver);          // fluent: returns the next screen
    }
}

public final class HomeScreen {
    private final AppiumDriver driver;
    private final Interactions ui;
    public HomeScreen(AppiumDriver driver) { this.driver = driver; this.ui = new Interactions(driver); }

    public ResultsScreen search(String query) {
        ui.type(Screens.SEARCH_FIELD, query + "\n");
        return new ResultsScreen(driver);
    }
}

public final class ResultsScreen {
    private final AppiumDriver driver;
    private final Interactions ui;
    public ResultsScreen(AppiumDriver driver) { this.driver = driver; this.ui = new Interactions(driver); }

    /** A question the test can assert on - returns data, performs no checks. */
    public int resultCount() {
        ui.waitVisible(Screens.RESULT_ROW);                 // wait for the list to render
        return driver.findElements(Screens.RESULT_ROW).size();
    }
}
```

```java
// 4. TEST - owns data + assertions, reads like a story
class SearchTest extends BaseTest {
    @Test
    @DisplayName("A logged-in user sees results for a known query")
    void search_returns_results() {
        int count = new LoginScreen(driver)
                .loginAs("standard_user", "secret")
                .search("widget")
                .resultCount();

        assertTrue(count > 0, "expected at least one result");
    }
}
```

`BaseTest` builds the driver via `DriverFactory`, stores it in `Drivers`
(ThreadLocal), uses **no** `implicitlyWait`, and quits in teardown.

---

## Review checklist

- [ ] No class mixes locators, actions, waits, **and** assertions.
- [ ] Locators are `By` constants kept apart from action code; `accessibilityId`
      preferred over XPath.
- [ ] Screen methods are **intentions**, not element getters; no assertions inside.
- [ ] No `implicitlyWait`, no `Thread.sleep` - explicit waits only.
- [ ] Typed to `AppiumDriver`, built via a factory; no scattered `new AndroidDriver`.
- [ ] Driver held in `ThreadLocal`; tests independent and order-free.
- [ ] Capabilities and server URL come from config, not hardcoded.
- [ ] Gesture math lives in one helper, not copy-pasted.
- [ ] Selenium is not declared directly, but its transitive version is pinned
      (Selenium BOM) to one compatible with the java-client (currently <= 4.33.0).
- [ ] No speculative abstraction (YAGNI); no copy-paste past the rule of three (DRY).
- [ ] Tests are self-verifying, isolated, and repeatable (F.I.R.S.T.); no manual
      screenshot or log inspection decides pass/fail.
- [ ] Each test reads top-to-bottom as Build-Operate-Check, like a sentence in the domain.
- [ ] UI tests cover critical journeys only; business logic is verified below the UI (test pyramid).
