package com.atd.test;

import com.google.common.collect.ImmutableMap;
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
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;


public class SampleTest {
    public AppiumDriver driver;
    AppiumDriverLocalService service;
    @BeforeMethod
    public void setUp() throws MalformedURLException {
        // Start server once before all tests //
        // Command-timeout 60 seconds //
//        service =
//                 AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
//                .usingAnyFreePort()
//                .withTimeout(Duration.ofSeconds(60))
//                .withLogFile(new File(System.getProperty("user.dir") + "/appiumServerLogs.txt"))
//                .withArgument(GeneralServerFlag.BASEPATH, "/wd/hub"));
//            service.clearOutPutStreams();
//            service.start();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 700000);
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2");
            capabilities.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir") + "/VodQA.apk");
            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        }

    @Test
    public void horizontalSwipeTest() {
        driver.findElement(AppiumBy.accessibilityId("login")).click();
        driver.findElement(AppiumBy.accessibilityId("dragAndDrop")).click();

        RemoteWebElement source = (RemoteWebElement) driver.findElement(AppiumBy.accessibilityId("dragMe"));
        RemoteWebElement destination = (RemoteWebElement) driver.findElement(AppiumBy.accessibilityId("dropzone"));

        driver.addCommand(HttpMethod.POST, String.format("/session/%s/plugin/actions/dragAndDrop",
                driver.getSessionId()), "dragAndDrop");
        driver.execute("dragAndDrop",
                ImmutableMap.of("sourceId", source.getId(),
                                "destinationId", destination.getId()));

//        final Point location = slider.getLocation();
//        final PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
//        final Sequence sequence = new Sequence(finger, 1);
//        sequence.addAction(finger.
//                createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), location.x, location.y));
//        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
//        sequence.addAction(new Pause(finger, Duration.ofMillis(600)));
//        sequence.addAction(finger.
//                createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), location.x + 400, location.y));
//        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
//
//        driver.perform(Collections.singletonList(sequence))
    }

    @Test
    public void horizontalSwipeTest1() {
        driver.findElement(AppiumBy.accessibilityId("login")).click();
        driver.findElement(AppiumBy.accessibilityId("dragAndDrop")).click();

        RemoteWebElement source = (RemoteWebElement) driver.findElement(AppiumBy.accessibilityId("dragMe"));
        RemoteWebElement destination = (RemoteWebElement) driver.findElement(AppiumBy.accessibilityId("dropzone"));

        driver.addCommand(HttpMethod.POST, String.format("/session/%s/plugin/actions/dragAndDrop",
                driver.getSessionId()), "dragAndDrop");
        driver.execute("dragAndDrop",
                ImmutableMap.of("sourceId", source.getId(),
                        "destinationId", destination.getId()));

//        final Point location = slider.getLocation();
//        final PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
//        final Sequence sequence = new Sequence(finger, 1);
//        sequence.addAction(finger.
//                createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), location.x, location.y));
//        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
//        sequence.addAction(new Pause(finger, Duration.ofMillis(600)));
//        sequence.addAction(finger.
//                createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), location.x + 400, location.y));
//        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
//
//        driver.perform(Collections.singletonList(sequence))
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
