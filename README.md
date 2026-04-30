# Sample Project

## Pre Requisites
* Make sure you follow the installation instruction [here](https://github.com/saikrishna321/VodQa_MobileAutomationWorkShop) and configure all the necessary dependencies required.
    * Java 17
    * Maven
    * Node 22+ installed
    * Android Studio
    * Android Emulators created
    * Make sure PATH variables are configured.
    * Appium Doctor and run the safety check
    * Appium 3 and it's driver's installed
    * Intellij IDE, etc

## IDE Setup (Cursor / VS Code)

If you are using **Cursor** or **Visual Studio Code** instead of IntelliJ, install the following extensions to get full Java + Maven + TestNG support:

| Extension | Publisher | Purpose |
| --- | --- | --- |
| Language Support for Java(TM) by Red Hat | Red Hat | Java linting, IntelliSense, formatting, refactoring, Maven/Gradle support |
| Gradle for Java | vscjava | Manage Gradle projects, run Gradle tasks, file authoring |
| Debugger for Java | vscjava | Lightweight Java debugger |
| Project Manager for Java | vscjava | Manage Java projects in the workspace |
| Extension Pack for Java | vscjava | Bundle of popular Java extensions (IntelliSense, debug, Maven, etc.) |
| Test Runner for Java | vscjava | Run and debug JUnit / TestNG test cases |
| Maven for Java | vscjava | Manage Maven projects, run goals, generate from archetype |

The easiest path is to install **Extension Pack for Java**, which bundles most of the above.

### Quick install (CLI)
```bash
code --install-extension vscjava.vscode-java-pack
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
```
Replace `code` with `cursor` if you are on Cursor IDE.

After installing, reload the window and let the Java extension import the Maven project — you should then be able to run `SampleTest` directly from the Test Runner side panel.

## Steps to execute
1. Start Appium server manually in terminal using `appium server -ka 800 -pa /wd/hub`
2. Start an Android emulator and confirm device connection using `adb devices` command
3. Open another terminal and navigate to the project
4. Run command `mvn clean test` 
