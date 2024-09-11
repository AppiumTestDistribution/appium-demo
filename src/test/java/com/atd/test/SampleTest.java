package com.atd.test;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;



public class SampleTest {
    public AppiumDriver driver;
    @BeforeClass
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName("Android Emulator");
        options.setNewCommandTimeout(Duration.ofSeconds(700000));
        options.setAutomationName("UIAutomator2");
        options.setApp(System.getProperty("user.dir") + "/VodQA.apk");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
    }

    @Test
    public void SampleTest() {
        driver.findElement(AppiumBy.accessibilityId("login")).click();
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    void iOSDriverCreation() throws MalformedURLException {
        XCUITestOptions options = new XCUITestOptions();
        options.setApp("https://github.com/AppiumTestDistribution/appium-demo/blob/main/vodqa.zip?raw=true");
        options.setDeviceName("iPhone 14 Pro");
        options.setPlatformName("iOS");
        options.setPlatformVersion("16.1");
        options.setAutomationName("XCuiTest");
        driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"),options);
    }
}
