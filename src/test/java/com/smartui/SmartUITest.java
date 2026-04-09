package com.smartui;

import io.github.lambdatest.SmartUISnapshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.HashMap;

public class SmartUITest {

    private WebDriver driver;

    private static final String LT_USERNAME = System.getenv("LT_USERNAME");
    private static final String LT_ACCESS_KEY = System.getenv("LT_ACCESS_KEY");
    private static final String HUB_URL = "https://" + LT_USERNAME + ":" + LT_ACCESS_KEY + "@hub.lambdatest.com/wd/hub";

    @BeforeTest
    public void setUp() throws Exception {
        HashMap<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("username", LT_USERNAME);
        ltOptions.put("accessKey", LT_ACCESS_KEY);
        ltOptions.put("project", "SmartUI Gradle");
        ltOptions.put("build", "SmartUI Build");
        ltOptions.put("name", "SmartUI Snapshot Test");
        ltOptions.put("smartUIProjectName", "LT-gradle");
        ltOptions.put("w3c", true);

        ChromeOptions options = new ChromeOptions();
        options.setBrowserVersion("latest");
        options.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL(HUB_URL), options);
    }

    @Test
    public void basicSnapshotTest() throws Exception {
        driver.get("https://dyson.com");
        SmartUISnapshot.smartuiSnapshot(driver, "Homepage");
        Thread.sleep(5000);
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
