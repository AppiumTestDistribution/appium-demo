package com.atd.test;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.net.MalformedURLException;
import java.time.Duration;

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
        }

    @Test
    public void SampleTest() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(presenceOfElementLocated(AppiumBy.accessibilityId("login"))).click();
    }

    @AfterSuite
    public void tearDown() {
        driver.quit();
        service.stop();
    }
}
