# SmartUI Selenium Java SDK - Gradle Setup

Visual regression testing using LambdaTest SmartUI with Selenium Java SDK, built with Gradle.

---

## Prerequisites

- Java 11+
- Node.js & npm
- Gradle 9+
- LambdaTest account with SmartUI access

---

## Project Structure

```
smartUI-gradle/
├── build.gradle
├── settings.gradle
├── .smartui.json
├── .gitignore
└── src/test/
    ├── java/com/smartui/
    │   └── SmartUITest.java
    └── resources/
        └── testng.xml
```

---

## Step-by-Step Setup

### Step 1: Create Project Structure

Created a Gradle Java project with the following layout:

```
src/test/java/com/smartui/   → test source files
src/test/resources/          → testng.xml suite config
```

---

### Step 2: Configure build.gradle

Added dependencies for SmartUI SDK, Selenium, and TestNG:

```groovy
plugins {
    id 'java'
}

group = 'com.smartui'
version = '1.0.0'

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.github.lambdatest:lambdatest-java-sdk:1.0.7'
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.18.1'
    testImplementation 'org.testng:testng:7.9.0'
}

test {
    useTestNG() {
        suites 'src/test/resources/testng.xml'
    }
}
```

> **Note:** `sourceCompatibility` must be set inside the `java {}` block in Gradle 9+.

---

### Step 3: Configure SmartUI (.smartui.json)

Generated the SmartUI config using:

```bash
npx smartui config:create .smartui.json
```

Viewports must be arrays of arrays (e.g., `[[1920], [1366], [1028]]`):

```json
{
  "web": {
    "browsers": ["chrome", "firefox", "safari", "edge"],
    "viewports": [
      [1920],
      [1366],
      [1028]
    ]
  },
  "mobile": {
    "devices": ["iPhone 14", "Galaxy S24"],
    "fullPage": true,
    "orientation": "portrait"
  },
  "waitForTimeout": 1000,
  "enableJavaScript": false,
  "allowedHostnames": [],
  "smartIgnore": false,
  "showRenderErrors": false
}
```

---

### Step 4: Configure TestNG Suite (testng.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="SmartUI Suite" verbose="1">
    <test name="SmartUI Tests">
        <classes>
            <class name="com.smartui.SmartUITest"/>
        </classes>
    </test>
</suite>
```

---

### Step 5: Write the Test (SmartUITest.java)

Used `ChromeOptions` with W3C capabilities (no deprecated `DesiredCapabilities`) to connect to LambdaTest cloud and capture SmartUI snapshots:

```java
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

    private static final String LT_USERNAME = System.getenv("LT_USERNAME") != null
            ? System.getenv("LT_USERNAME") : "your-username";
    private static final String LT_ACCESS_KEY = System.getenv("LT_ACCESS_KEY") != null
            ? System.getenv("LT_ACCESS_KEY") : "your-access-key";
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
```

---

### Step 6: Install SmartUI CLI

```bash
npm install @lambdatest/smartui-cli
```

---

### Step 7: Set Environment Variables

```bash
# macOS/Linux
export PROJECT_TOKEN="your-smartui-project-token"
export LT_USERNAME="your-lambdatest-username"
export LT_ACCESS_KEY="your-lambdatest-access-key"

# Windows CMD
set PROJECT_TOKEN="your-smartui-project-token"
set LT_USERNAME="your-lambdatest-username"
set LT_ACCESS_KEY="your-lambdatest-access-key"
```

Get your:
- **PROJECT_TOKEN** → SmartUI Dashboard → Project Settings
- **LT_USERNAME / LT_ACCESS_KEY** → [LambdaTest Account Settings](https://accounts.lambdatest.com/security)

---

### Step 8: Run Tests

```bash
PROJECT_TOKEN="your-project-token" \
LT_USERNAME="your-username" \
LT_ACCESS_KEY="your-access-key" \
npx smartui --config .smartui.json exec -- gradle test
```

---

## How It Works

1. `npx smartui exec` authenticates with SmartUI and creates a build
2. `gradle test` compiles and runs the TestNG suite
3. `SmartUISnapshot.smartuiSnapshot(driver, "Screenshot Name")` captures a visual snapshot via the LambdaTest cloud session
4. SmartUI processes the snapshot and compares it against the baseline
5. Results are available in the SmartUI dashboard

---

## Viewing Results

After each run, a build URL is printed in the console:

```
build url: https://smartui.lambdatest.com/builds/<project-id>?searchBuild=<build-id>
```

Open that URL to review snapshot comparisons, approve baselines, and track visual regressions.
