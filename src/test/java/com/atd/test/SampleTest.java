package com.atd.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import org.testng.Assert;
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
import java.util.List;
import java.util.Map;

public class SampleTest {
    public AppiumDriver driver;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public void setUp() throws MalformedURLException {
        // iOSDriverCreation();
        // UiAutomator2Options options = new UiAutomator2Options();
        // options.setAutomationName("UIAutomator2");
        // options.setNewCommandTimeout(Duration.ofSeconds(30));
        // driver = new AndroidDriver(new URL("http://127.0.0.1:4040/wd/hub"), options);

          XCUITestOptions options = new XCUITestOptions();
        options.setAutomationName("XCUITest");
        options.setNewCommandTimeout(Duration.ofSeconds(30));
        options.setWebDriverAgentUrl("http://127.0.0.1:8100");
        options.setUdid("");
        driver = new IOSDriver(new URL("http://127.0.0.1:4040/wd/hub"), options);
    }

    @Test
    public void testWithSwipeAndAssertions() throws JsonProcessingException, InterruptedException {
        AIResponse response = getAIResponse("1.Can you get the time left from the first match?" + 
        "2. Can you get me the left Team name abbervation from the first match" +
        "3. Can you see verify Win 2Lakhs, Royal Enfield, iPhone. return only Yes or No" + 
        "4. Can you get the match start time?" +
        "Just give me the answer in array with no explanation and also explanation as array");
        String[] answerArray = response.getAnswerAsArray();
        String[] explanationArray = response.getExplanationAsArray();
        
        // Add assertions
        Assert.assertNotNull(answerArray, "Answer array should not be null");
        Assert.assertEquals(answerArray.length, 4, "Answer array should contain 2 elements");
        
        // Verify time format (first element)
        String timeLeft = answerArray[0];
        System.out.println(timeLeft);
        System.out.println(answerArray[2]);
        Assert.assertTrue(timeLeft.matches("\\d{1,2}h \\d{1,2}m"), 
            "Time format should be in the format: XXh XXm. Actual: " + timeLeft);
        
        // Extract hours and minutes for additional validation
        String[] timeParts = timeLeft.split(" ");
        int hours = Integer.parseInt(timeParts[0].replace("h", ""));
        int minutes = Integer.parseInt(timeParts[1].replace("m", ""));
        
        // Validate hours and minutes are within valid ranges
        Assert.assertTrue(hours >= 0 && hours <= 24, "Hours should be between 0 and 24");
        Assert.assertTrue(minutes >= 0 && minutes < 60, "Minutes should be between 0 and 59");
        
        // Verify team abbreviation (second element)
        Assert.assertEquals(answerArray[1], "SIX-W", "Team abbreviation should match expected value");
        Assert.assertEquals(answerArray[2], "Yes", "Win 2Lakhs, Royal Enfield, iPhone Not seen!");
        Assert.assertEquals(answerArray[3], "10:15 AM", explanationArray[3]);
        System.out.println("Explanation: " + explanationArray[3]);
        ai("Click on first match tile");

        AIResponse allContests = getAIResponse( 
        "1. Can you verify 34.2 Lakhs shown in first contest tile is placed at left top corner" +
        "2. Can you verify if the first contest has tag Mega Contest."+
        "Just give me the answer in array as Yes or No and also explanation as array");
        String[] allContestsAnswer = allContests.getAnswerAsArray();
        String[] allContestsExplanation = allContests.getExplanationAsArray();
        Assert.assertEquals(allContestsAnswer[0], "Yes", allContestsExplanation[0]);
        Assert.assertEquals(allContestsAnswer[1], "Yes", allContestsExplanation[1]);
        ai("Click on 34.2 Lakhs in first tile");
        AIResponse cashPrice = getAIResponse("what is the text below Prizes");
        String priceValue = cashPrice.getAnswerAsString();
        Assert.assertEquals(priceValue, "₹2 Lakhs");
        ai("Scroll up until you see Disclaimer", 5, "MEDIUM");
    }


    /**
 * Gets AI information and parses the response
 * 
 * @param instruction The instruction to send to AI
 * @return AIResponse containing the parsed response
 * @throws JsonProcessingException if JSON parsing fails
 */
protected AIResponse getAIResponse(String instruction) throws JsonProcessingException {
    Object result = aiGetInfo(instruction);

    // If `result` is already a Map, skip conversion
    Map<String, Object> jsonMap;
    if (result instanceof Map) {
        jsonMap = (Map<String, Object>) result;
    } else if (result instanceof String) {
        // If `result` is a JSON string, parse it
        jsonMap = mapper.readValue((String) result, new TypeReference<Map<String, Object>>() {});
    } else {
        throw new IllegalArgumentException("Unexpected response type: " + result.getClass());
    }

    // Handle `answer` field
    Object answerObj = jsonMap.get("answer");
    Object answer;
    if (answerObj instanceof String) {
        answer = answerObj;
    } else if (answerObj instanceof List) {
        @SuppressWarnings("unchecked")
        List<String> answerList = (List<String>) answerObj;
        answer = answerList.toArray(new String[0]); // Convert the list to an array of String
    } else {
        throw new IllegalArgumentException("Unexpected type for 'answer': " + (answerObj != null ? answerObj.getClass() : "null"));
    }

    // Handle `explanation` field
    Object explanationObj = jsonMap.get("explanation");
    Object explanation;
    if (explanationObj instanceof String) {
        explanation = explanationObj;
    } else if (explanationObj instanceof List) {
        @SuppressWarnings("unchecked")
        List<String> explanationList = (List<String>) explanationObj;
        explanation = explanationList.toArray(new String[0]); // Convert the list to an array of String
    } else {
        throw new IllegalArgumentException("Unexpected type for 'explanation': " + (explanationObj != null ? explanationObj.getClass() : "null"));
    }

    return new AIResponse(answer, explanation);
}

    

    private Object ai(String instruction) {
        return ai(instruction, null, null, null, null);
    }

    private Object ai(String instruction, int maxScrolls) {
        return ai(instruction, null, null, maxScrolls, null);
    }

    private Object ai(String instruction, int maxScrolls, String scrollSize) {
        return ai(instruction, null, null, maxScrolls, scrollSize);
    }
    
    private Object ai(String instruction, Boolean saveToCache, Boolean elementVisibleCheck, Integer maxScrolls, String scrollSize) {
        // Set default values for optional parameters
        if (saveToCache == null) {
            saveToCache = false; // Default value
        }
        if (elementVisibleCheck == null) {
            elementVisibleCheck = false; // Default value
        }
        if (maxScrolls == null) {
            maxScrolls = 3; // Default value
        }
        if (scrollSize == null) {
            scrollSize = "SMALL"; // Default value
        }
    
        Map<String, Object> args = new HashMap<>();
        args.put("instruction", instruction);
    
        // Options map with parameters
        Map<String, Object> options = new HashMap<>();
        options.put("saveToCache", saveToCache);
        options.put("elementVisibleCheck", elementVisibleCheck);
        options.put("maxScrolls", maxScrolls);
        options.put("scrollSize", scrollSize);
    
        // Add options to args
        args.put("options", options);
    
        // Execute the Appium script
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
