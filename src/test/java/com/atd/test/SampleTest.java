package com.atd.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


public class SampleTest {
    public AppiumDriver driver;
    private final ObjectMapper mapper = new ObjectMapper();
    @BeforeClass
    public void setUp() throws MalformedURLException {
        //iOSDriverCreation();
       UiAutomator2Options options = new UiAutomator2Options();
       options.setNewCommandTimeout(Duration.ofSeconds(700000));
       options.setAutomationName("UIAutomator2");
       driver = new AndroidDriver(new URL("http://127.0.0.1:4040/wd/hub"), options);
    }

    // @Test
    // public void SampleTest1() {
    //  //System.out.println(aiGetInfo("Get me the username field text?"));
    //    ai("Click on 'Where are you going?'");
    //    ai("Enter for 'HSR Layout' in the Drop location field");
    //    ai("Click on the 'HSR Layout below Select on Map'");
    //    String result = (String) aiGetInfo("Can you see bikes in the map?");
    //    System.out.println(result);
    //    ai("Click on the 'current location icon navigation icon'");
    //    ai("Click on Edit icon");
    // }

    @Test
    public void SampleTest2() throws JsonProcessingException {
       ai("Click on 'Where are you going?'");
       ai("Enter for 'HSR Layout' in the Drop location field");
       ai("Click on the 'Heart icon of HSR Layout below Select on Map'");
       AIResponse response = getAIResponse("Can you see 'HSR Layout below Add to favourites'?");
       System.out.println("Condition Satisfied: " + response.isConditionSatisfied());
       System.out.println("Explanation: " + response.getExplanation());
    }

        /**
     * Gets AI information and parses the response
     * @param instruction The instruction to send to AI
     * @return AIResponse containing the parsed response
     * @throws JsonProcessingException if JSON parsing fails
     */
    protected AIResponse getAIResponse(String instruction) throws JsonProcessingException {
        String result = (String) aiGetInfo(instruction);
        Map<String, Object> jsonMap = mapper.readValue(result, new TypeReference<Map<String, Object>>() {});
        
        boolean conditionSatisfied = (Boolean) jsonMap.get("conditionSatisfied");
        String explanation = (String) jsonMap.get("explanation");
        
        return new AIResponse(conditionSatisfied, explanation);
    }

    private Object ai(String instruction) {
        Map<String, Object> args = new HashMap<>();
        args.put("instruction", instruction);
        Map<String, Object> options = new HashMap<>();
        options.put("saveToCache", false);
        args.put("options", options);
        return driver.executeScript("vision: findByAI", args);
    }

    private Object aiGetInfo(String instruction) {
        Map<String, Object> args = new HashMap<>();
        args.put("instruction", instruction);
        return driver.executeScript("vision: getInfo", args);
    }
    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}















