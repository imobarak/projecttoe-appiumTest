/*import io.appium.java_client.MobileElement;
import org.junit.After;
import org.junit.Before;
import io.appium.java_client.ios.IOSDriver;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.testng.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.WebElement;*/
import java.net.MalformedURLException;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

import java.net.URL;
import java.util.List;


import org.openqa.selenium.NoSuchElementException;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.annotations.*;
import org.testng.Assert;

/**
 * Created by imobarak on 3/31/16.
 */
public class PTTest {
    IOSDriver driver;
    DesiredCapabilities capabilities;
    WebElement nav_bar;
    //should have @Before @After if tests are independent and i'd want
    // to execute certain actions before/after every test
    //but in our case they depend on login to continue
    @BeforeGroups("allTests")
    public void setup() throws MalformedURLException {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName","myphone");
        capabilities.setCapability("autoAcceptAlerts", true);
         driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);
        System.out.println("setup done");

    }

    @AfterTest(groups = "allTests", alwaysRun = true )
    public void teardown(){
        driver.quit();
        System.out.println("clean up done");

    }

    @Test(priority=1, groups = "allTests")
    public void loginWithInvalidCredentials() throws MalformedURLException {
        driver.findElement(By.name("Sign In")).click();
        capabilities.setCapability("autoAcceptAlerts", false);
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys("invalidUsername");
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).sendKeys("password");
        driver.findElement(By.name("SUBMIT")).click();

        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        Alert errorDialog = driver.switchTo().alert();
        Assert.assertEquals(errorDialog.getText(),"Login failed. Username and/or Password incorrect");
        errorDialog.accept();
        System.out.println("invalidLogin done");
    }

    @Test(priority=2, groups = "allTests")
    public void loginWithValidCredentials() throws MalformedURLException {
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).clear();
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).clear();
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys("imobarak3");
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).sendKeys("sky");
        driver.findElement(By.name("SUBMIT")).click();
        MobileElement successView = (MobileElement)new WebDriverWait(driver,15).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//UIANavigationBar/UIAStaticText")));
        System.out.println("validlogin done");

       /* try{
            isNewsFeedTab();
            System.out.println("go to newsfeed success");
            try {
                loadNewsfeed();
                System.out.println("load newsfeed success");
            } catch (Exception e) {
                System.out.println("loadNewsFeed failed");
            }
            try {
                //makePost();
                //new WebDriverWait(driver,15).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//UIATableView")));
                //checkPostSuccessful();
                //System.out.println("post success");
                //try {
                //    hugPost();
               //     System.out.println("hug post success");
              //  } catch (Exception e) {
              //      System.out.println("hug post failed");
             //   }
                try {

                    commentPost();
                    System.out.println("comment post success");
                } catch (Exception e) {
                    System.out.println("comment post failed");
                }
            } catch (Exception e) {
                System.out.println("post failed");
            }
        }catch  (Exception e){
            System.out.println("go to newsfeed failed");
        }
*/
    }

    @Test( groups = "allTests", dependsOnMethods = {"loginWithValidCredentials"}, priority = 3)
    public void isNewsFeedTab() throws Exception {
        nav_bar = null;
        //there is nav bar inside the app
        driver.getPageSource();
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        Assert.assertNotNull(nav_bar);
        Assert.assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(),"Newsfeed");
        System.out.println("isNewsfeed done");

    }

    @Test(groups = "allTests", dependsOnMethods = {"isNewsFeedTab"}, priority = 4)
    public void loadNewsfeed() throws Exception {
        //first view in UICatalog is a table
        IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
        Assert.assertNotNull(table);
        //is number of cells/rows inside table correct
        List<MobileElement> rows = table.findElementsByClassName("UIATableCell");
        Assert.assertEquals(10, rows.size());
        //is the username loaded correctly for now i just check if not empty
        Assert.assertNotSame("", rows.get(0).findElement(By.className("UIAStaticText")).getText());
        System.out.println("loadNewsfeed");

    }

    @Test( groups = "allTests", dependsOnMethods = {"isNewsFeedTab"}, priority=5)
    public void makePost() throws Exception {
        nav_bar.findElement(By.name("Post")).click();
        driver.findElementByClassName("UIATextView").sendKeys("Testing using Appium");
        nav_bar.findElement(By.name("Post")).click();
        System.out.println("makepost");
        checkPostSuccessful();
    }

    public void checkPostSuccessful() throws Exception {
        //first view in UICatalog is a table
        IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
        Assert.assertNotNull(table);
        //is number of cells/rows inside table correct
        List<MobileElement> rows = table.findElementsByClassName("UIATableCell");

       //check that username is correct and message is correct
       WebElement element = rows.get(0).findElements(By.className("UIAStaticText")).get(0);
        Assert.assertEquals("imobarak3", element.getText());
        element = rows.get(0).findElements(By.className("UIAStaticText")).get(1);
        Assert.assertEquals("Testing using Appium", element.getText());
        System.out.println("checkpostsuccessful");

    }

    @Test( groups = "allTests", dependsOnMethods = {"isNewsFeedTab"}, priority = 7)
    public void hugPost() throws Exception {
        //first view in UICatalog is a table
        IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
        List<MobileElement> rows = table.findElementsByClassName("UIATableCell");
        WebElement element = rows.get(0).findElements(By.className("UIAButton")).get(2);
        Integer numOfHugsBefore =Integer.parseInt(element.getText().substring(0,1));
        rows.get(0).findElements(By.className("UIAButton")).get(1).click();

        new WebDriverWait(driver,15);

        Integer numOfHugsAfter =Integer.parseInt(element.getText().substring(0,1));
        Assert.assertEquals(new Long(numOfHugsAfter), new Long(numOfHugsBefore + 1));
        System.out.println("hugpost");

    }

    @Test( groups = "allTests", dependsOnMethods = {"isNewsFeedTab"}, priority = 8)
    public void commentPost() throws Exception {
        IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
        List<MobileElement> rows = table.findElementsByClassName("UIATableCell");
        rows.get(0).findElements(By.className("UIAButton")).get(0).click();

        //first view in UICatalog is a table
        Integer numOfCellsBefore = ((List<MobileElement>)driver.findElementsByXPath("//UIATableView[1]/UIATableCell")).size();
        table = (IOSElement)driver.findElementsByClassName("UIATableView").get(1);
        rows = table.findElementsByClassName("UIATableCell");
        rows.get(0).findElement(By.className("UIATextView")).sendKeys("Comment using Appium");
        rows.get(0).findElement(By.className("UIAButton")).click();
        new WebDriverWait(driver,15);
        table = (IOSElement)driver.findElementsByClassName("UIATableView").get(0);
        rows = table.findElementsByClassName("UIATableCell");
        Assert.assertEquals(numOfCellsBefore + 1, rows.size());
        System.out.println("comment post");
    }
}
