package com.testappium1;

import io.appium.java_client.android.AndroidDriver;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.appium.java_client.remote.MobileCapabilityType;

/**
 * Automated tests for New Wallet flow.
 */
public class AppTest 
{
    public AndroidDriver driver;
    
    public ExtentHtmlReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest test;

    public void enterPasscode(String code){
        for (int i = 0; i < code.length(); i++) {
            String xpathExpression = String.format("//android.widget.TextView[@text='%s']", code.charAt(i));
            WebElement key = driver.findElement(By.xpath(xpathExpression));
            key.click();
        }
    }

    public void acceptConsentPage() throws InterruptedException{
        WebElement consent1 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/concent1"));
        WebElement check1 = consent1.findElement(By.className("android.widget.CheckBox"));
        check1.click();

        WebElement consent2 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/concent2"));
        WebElement check2 = consent2.findElement(By.className("android.widget.CheckBox"));
        check2.click();

        WebElement consent3 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/concent3"));
        WebElement check3 = consent3.findElement(By.className("android.widget.CheckBox"));
        check3.click();

        Thread.sleep(2000);
    }

    @BeforeTest
    public void setExtent() {
        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/myReport.html");

        htmlReporter.config().setDocumentTitle("Automation Report");
        htmlReporter.config().setReportName("Functional Report");
        htmlReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();

        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Hostname", "LocalHost");
        extent.setSystemInfo("OS", "Windows 10");
        extent.setSystemInfo("Tester Name", "Saulex");
        extent.setSystemInfo("Browser", "Chrome");
    }

    @BeforeMethod
    public void before() throws Exception {
        // Configure and Setup the Device
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "13");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2");

        // Setting up the TrustWallet package and activity
        capabilities.setCapability("appPackage", "com.wallet.crypto.trustapp");
        capabilities.setCapability("appActivity", "com.wallet.crypto.trustapp.ui.start.activity.RootHostActivity");

        // Opening device with TrustWallet app
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    /**
     * User should not be able to create a New Wallet if he doesn't accept Legal Policy
     */
    @Test(priority = 1)
    public void continueButtonShouldBeDisabledIfLegalPolicyNotAccepted()
    {
        test = extent.createTest("continueButtonShouldBeDisabledIfLegalPolicyNotAccepted");

        // On Welcome Page click 'Create New Wallet' button
        WebElement newAccountButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/new_account_action"));
        newAccountButton.click();

        // Continue button should be disabled in Legal Page
        WebElement nextButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/next"));                
        Assert.assertTrue(!nextButton.isEnabled(), "Continue button should be disabled in Legal Page");
    }

    /**
     * User should not be able to create a New Wallet if he doesn't confirm the Passcode
     */
    @Test(priority = 2)
    public void errorMessageShouldBeDisplayedWhenPasscodeDoesNotMatch()
    {
        test = extent.createTest("errorMessageShouldBeDisplayedWhenPasscodeDoesNotMatch");

        // On Welcome Page click 'Create New Wallet' button
        WebElement newAccountButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/new_account_action"));
        newAccountButton.click();

        // Check 'I've read and accept the forms of Service and Privacy Policy' checkbox
        WebElement checkBox = driver.findElement(By.id("com.wallet.crypto.trustapp:id/acceptCheckBox"));
        checkBox.click();

        // Click Continue button
        WebElement nextButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/next"));
        nextButton.click();

        // Enter a valid Passcode
        enterPasscode("000000");        
        
        // Enter an incorrect Passcode
        enterPasscode("111111");
                
        // 'Those passwords didn't match' error message should be displayed
        WebElement errorMessage = driver.findElement(By.xpath("//android.widget.TextView[contains(@text,\"Those passwords didn’t match!\")]"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");
    }

    /**
     * User should not be able to create a New Wallet if he doesn't confirm the Secret Phrase
     * @throws InterruptedException
     */
    @Test(priority = 3)
    public void errorMessageShouldBeDisplayedWhenSecretPhraseDoesNotMatch() throws InterruptedException
    {
        test = extent.createTest("errorMessageShouldBeDisplayedWhenSecretPhraseDoesNotMatch");

        // On Welcome Page click 'Create New Wallet' button
        WebElement newAccountButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/new_account_action"));
        newAccountButton.click();

        // Check 'I've read and accept the forms of Service and Privacy Policy' checkbox
        WebElement checkBox = driver.findElement(By.id("com.wallet.crypto.trustapp:id/acceptCheckBox"));
        checkBox.click();

        // Click Continue button
        WebElement nextButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/next"));
        nextButton.click();

        // Enter a valid Passcode
        enterPasscode("000000");        

        // Confirm valid Passcode
        enterPasscode("000000");        

        // Accept Consent Screen by checking all displayed checkboxes
        acceptConsentPage();        

        // Click Continue button
        WebElement nextButton2 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/next"));
        nextButton2.click();        

        // Save and Store Secret Phrase
        WebElement phraseGroup1 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/phrase"));
        List<WebElement> allChildElements = phraseGroup1.findElements(By.className("android.widget.LinearLayout"));

        HashMap<String, String> keywords = new HashMap<String, String>();
        
        for (WebElement we : allChildElements) {
            WebElement pp = we.findElement(By.id("com.wallet.crypto.trustapp:id/position"));
            WebElement pv = we.findElement(By.id("com.wallet.crypto.trustapp:id/value"));
            
            keywords.put(pp.getText(), pv.getText());            
        }

        // Click Continue button
        WebElement nextButton3 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/action_verify"));
        nextButton3.click();        
    
        // Click an incorrect word from the Phrase
        WebElement wordsGroup = driver.findElement(By.id("com.wallet.crypto.trustapp:id/words"));
        List<WebElement> valueElements = wordsGroup.findElements(By.className("android.widget.TextView"));

        for (WebElement ve : valueElements) {
            if(keywords.get("3").equals(ve.getText())) {
                ve.click();
            }                        
        }
        
        // 'Invalid order. Try again!' error message should be displayed
        WebElement errorMessage = driver.findElement(By.xpath("//android.widget.TextView[contains(@text,'Invalid order. Try again!')]"));        
        Assert.assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");
    }

    /**
     * User should be able to create a New Wallet Successfully
     * @throws InterruptedException
     */
    @Test(priority = 4)
    public void newWalletWizardShouldBeDisplayedWhenNewWalletSucess() throws InterruptedException
    {
        test = extent.createTest("newWalletWizardShouldBeDisplayedWhenNewWalletSucess");

        // On Welcome Page click 'Create New Wallet' button
        WebElement newAccountButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/new_account_action"));
        newAccountButton.click();

        // Check 'I've read and accept the forms of Service and Privacy Policy' checkbox
        WebElement checkBox = driver.findElement(By.id("com.wallet.crypto.trustapp:id/acceptCheckBox"));
        checkBox.click();

        // Click Continue button
        WebElement nextButton = driver.findElement(By.id("com.wallet.crypto.trustapp:id/next"));
        nextButton.click();

        // Enter a valid Passcode
        enterPasscode("000000");        

        // Confirm valid Passcode
        enterPasscode("000000");        

        // Accept Consent Screen by checking all displayed checkboxes
        acceptConsentPage();        

        // Click Continue button
        WebElement nextButton2 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/next"));
        nextButton2.click();         

        // Save and Store Secret Phrase
        WebElement phraseGroup1 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/phrase"));
        List<WebElement> allChildElements = phraseGroup1.findElements(By.className("android.widget.LinearLayout"));

        HashMap<String, String> keywords = new HashMap<String, String>();
        
        for (WebElement we : allChildElements) {
            WebElement pp = we.findElement(By.id("com.wallet.crypto.trustapp:id/position"));
            WebElement pv = we.findElement(By.id("com.wallet.crypto.trustapp:id/value"));
            
            keywords.put(pp.getText(), pv.getText());            
        }

        // Click Continue button
        WebElement nextButton3 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/action_verify"));
        nextButton3.click();        
    
        // Click on every word and replicate correct Secret Phrase
        WebElement wordsGroup = driver.findElement(By.id("com.wallet.crypto.trustapp:id/words"));
        List<WebElement> valueElements = wordsGroup.findElements(By.className("android.widget.TextView"));

        for(int i=1; i<=keywords.keySet().size(); i++) {
            for (WebElement ve : valueElements) {
                if(keywords.get(Integer.toString(i)).equals(ve.getText())) {
                    ve.click();                         
                }                
            }            
            wordsGroup = driver.findElement(By.id("com.wallet.crypto.trustapp:id/words"));
            valueElements = wordsGroup.findElements(By.className("android.widget.TextView"));
        }
        
        // Click Done button
        WebElement nextButton4 = driver.findElement(By.id("com.wallet.crypto.trustapp:id/action_done"));
        nextButton4.click();        
        
        // New Wallet screen containint new message should be displayed        
        WebElement newWalletMessage = driver.findElement(By.xpath("//android.widget.TextView[contains(normalize-space(@text),\"Let's explore your new wallet!\")]"));
        Assert.assertTrue(newWalletMessage.isDisplayed(), "Explore your wallet message should be displayed");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getName());
            test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "TEST CASE PASSED IS " + result.getName());
        }        
    }

    @AfterTest
    public void after() throws Exception {        
        extent.flush();
        driver.quit();        
    }
}
