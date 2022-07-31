package com.atd.test;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Collections;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;


public class SampleTest {
    public AppiumDriver driver;
    WebDriverWait wait;
    AppiumDriverLocalService service;
    @BeforeSuite
    public void setUp() throws MalformedURLException {
        // Start server once before all tests //
        // Command-timeout 60 seconds //
        service =
                 AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingAnyFreePort()
                .withTimeout(Duration.ofSeconds(60))
                .withLogFile(new File(System.getProperty("user.dir") + "/appiumServerLogs.txt"))
                .withArgument(GeneralServerFlag.BASEPATH, "/wd/hub"));
            service.clearOutPutStreams();
            service.start();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 700000);
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2");
            capabilities.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir") + "/VodQA.apk");
            driver = new AndroidDriver(service.getUrl(), capabilities);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        }

    @Test
    public void horizontalSwipeTest() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(presenceOfElementLocated(AppiumBy.accessibilityId("login"))).click();
        wait.until(presenceOfElementLocated(AppiumBy.accessibilityId("slider1"))).click();
        WebElement slider = wait.until(presenceOfElementLocated(AppiumBy.accessibilityId("slider")));
        final Point location = slider.getLocation();

        // pointer move , pointer down, pause , pointer to new location, pointer up

        final PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        final Sequence sequence = new Sequence(finger, 1);
        sequence.addAction(finger.
                createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), location.x, location.y));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
        sequence.addAction(new Pause(finger, Duration.ofMillis(600)));
        sequence.addAction(finger.
                createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), location.x + 400, location.y));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));

        driver.perform(Collections.singletonList(sequence));

    }

    @AfterSuite
    public void tearDown() {
        driver.quit();
        service.stop();
    }
}
