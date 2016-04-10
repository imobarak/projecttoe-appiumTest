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
import org.testng.Assert.*;

/**
 * Created by imobarak on 3/31/16.
 */
public class PTTest {
    IOSDriver driver;
    DesiredCapabilities capabilities;
    WebElement nav_bar;
    WebElement tab_bar;
    //should have @Before @After if tests are independent and i'd want
    // to execute certain actions before/after every test
    //but in our case they depend on login to continue
   // @BeforeGroups("allTests")
    @BeforeTest(groups = "main")
    public void setup() throws MalformedURLException {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName","myphone");
        driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);
        System.out.println("setup done");
    }

    //@AfterTest(groups = "allTests", alwaysRun = true )
    @AfterTest(groups = "main", alwaysRun = true )
    public void teardown(){
        driver.quit();
        System.out.println("clean up done");
    }

    @Test(groups = "main", priority = 1)
    public  void signIn() throws Exception {
        WebElement element = driver.findElement(By.name("Sign In"));
        Assert.assertNotNull(element);
        element.click();
    }

    @Test(groups = "loginIssues", priority=2)
    public void forgotPasswordInvalidEmail()  throws MalformedURLException {
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        if(nav_bar.getAttribute("name").equals("Login")) {
            driver.findElement(By.xpath("//UIATableView/UIATableCell[3]/UIAButton[1]")).click();
        }

        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys("invalidemail");
        driver.findElement(By.xpath("//UIATableCell[2]/UIAButton")).click();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        Alert errorDialog = driver.switchTo().alert();
        Assert.assertEquals(errorDialog.getText(), "Please provide a valid E-mail Address");
        errorDialog.accept();
    }

    @Test(groups = "loginIssues", priority=3)
    public void forgotPasswordValidEmail()  throws MalformedURLException {
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        if(nav_bar.getAttribute("name").equals("Login")) {
            driver.findElement(By.xpath("//UIATableView/UIATableCell[3]/UIAButton[1]")).click();
        }
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).clear();
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys("imobarak@gmail.com");
        driver.findElement(By.xpath("//UIATableCell[2]/UIAButton")).click();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        Alert errorDialog = driver.switchTo().alert();
        Assert.assertEquals(errorDialog.getText(), "We'll send you an e-mail with your username and new password.");
        errorDialog.accept();
    }

    @Test(groups = "signup", priority=2)
    public void signUpValidValues()  throws MalformedURLException {
        try{
            driver.findElement(By.name("Sign In")).click();
        }catch (Exception e){
            //was not in sign in page
        }
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            if(nav_bar.getAttribute("name").equals("Forgot Password")) {
                nav_bar.findElement(By.name("BACK")).click();
            }
        }catch (Exception e){
            //no navigation bar
        }

        driver.findElement(By.name("Join Now!")).click();
        IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
        List<MobileElement> rows = table.findElementsByClassName("UIATableCell");
        rows.get(0).findElementByClassName("UIATextField").sendKeys("imobarak@gmail.com");
        rows.get(1).findElementByClassName("UIATextField").sendKeys("username");
        rows.get(2).findElementByClassName("UIASecureTextField").sendKeys("password");
        rows.get(3).findElementByClassName("UIASecureTextField").sendKeys("password");
        nav_bar.findElement(By.name("NEXT")).click();
        // TODO: incomplete function
    }

    @Test(groups = "loginIssues", priority=4)
    public void loginWithInvalidCredentials() throws MalformedURLException {
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

    @Test(groups = "login", priority=2)
    public void loginWithValidCredentials() throws MalformedURLException {
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).clear();
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).clear();
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys("imobarak3");
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).sendKeys("sky");
        driver.findElement(By.name("SUBMIT")).click();
        MobileElement successView = (MobileElement)new WebDriverWait(driver,15).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//UIANavigationBar/UIAStaticText")));
        System.out.println("validlogin done");
        tab_bar = driver.findElementByClassName("UIATabBar");

    }

    @Test(groups = "newsfeed", dependsOnMethods = {"loginWithValidCredentials"}, priority = 10)
    public void goToNewsFeedTab() throws Exception {
        //there is nav bar inside the app
        driver.getPageSource();
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        tab_bar = driver.findElementByClassName("UIATabBar");
        tab_bar.findElement(By.name("Newsfeed")).click();
        Assert.assertTrue(tab_bar.findElement(By.name("Newsfeed")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        Assert.assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(), "Newsfeed");

    }

    @Test(groups = "newsfeed", dependsOnMethods = {"goToNewsFeedTab"}, priority = 11)
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

    @Test(groups = "newsfeed", dependsOnMethods = {"goToNewsFeedTab"}, priority=12)
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

    @Test(groups = "newsfeed", dependsOnMethods = {"goToNewsFeedTab"}, priority = 13)
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

    @Test(groups = "newsfeed", dependsOnMethods = {"goToNewsFeedTab"}, priority = 14)
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

    @Test(groups = "groupsTab", priority = 20)
    public void goToGroupsTab() throws Exception {

        tab_bar.findElement(By.name("Groups")).click();
        Assert.assertTrue(tab_bar.findElement(By.name("Groups")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        Assert.assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(), "Support Groups");
    }

    @Test(groups = "groupsTab",  priority = 21)
    public void loadGroups() throws Exception {
        List<MobileElement> tableGroups = driver.findElements(By.xpath("//UIATableView/UIATableGroup"));
        //assert that there are 3 sections
        Assert.assertEquals(tableGroups.size(), 3);

        //assert that this section exists
        WebElement supportGroups = driver.findElementByName("YOUR SUPPORT GROUPS");
        Assert.assertNotNull(supportGroups);

        //assert that there are groups under this section - not empty
        List<MobileElement> cells = driver.findElements(By.xpath("//UIATableCell[preceding-sibling::UIATableGroup[1]/@name = 'YOUR SUPPORT GROUPS']"));
       Assert.assertNotEquals(cells.size(), 0);

        //assert that this section exists
        supportGroups = driver.findElementByName("RECOMMENDED SUPPORT GROUPS");
        Assert.assertNotNull(supportGroups);

        //assert that there are groups under this section - not empty
        cells = driver.findElements(By.xpath("//UIATableCell[preceding-sibling::UIATableGroup[1]/@name = 'RECOMMENDED SUPPORT GROUPS']"));
        Assert.assertNotEquals(cells.size(), 0);

    }

    @Test(groups = "requestsTab", priority = 30)
    public void goToRequestsTab() throws Exception {

        tab_bar.findElement(By.name("Request")).click();
        Assert.assertTrue(tab_bar.findElement(By.name("Request")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        Assert.assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(),"Friend Requests");
    }

    @Test(groups = "requestsTab",  priority = 31)
    public void loadRequests() throws Exception {
        if(!tab_bar.findElement(By.name("Request")).isSelected())
            tab_bar.findElement(By.name("Request")).click();
        //contacts tab shows only tableview and not the cells under it
        IOSElement tableView = (IOSElement)driver.findElement(By.xpath("//UIATableView"));
        List<MobileElement> tableElements = tableView.findElements(By.className("UIAElement"));
        //assert that the value matches
        //Assert.assertEquals(tableElements.get(0).getAttribute("name"), "FRIEND REQUESTS");
        //Assert.assertEquals(tableElements.get(1).getAttribute("name"), "RECOMMENDED USERS");

        List<MobileElement> tableCells = tableView.findElements(By.className("UIATableCell"));
        boolean friendRequests = false;
        boolean recommendedUsers = false;
        for (MobileElement element : tableCells) {
            try{
                element.findElementByName("Confirm");
                friendRequests = true;
            }catch (Exception e){
                try {
                    element.findElementByName("Add Friend");
                    recommendedUsers = true;
                }catch (Exception e1) {
                    //not buttons found
                }
            }
            if(friendRequests && recommendedUsers) {
                break;
            }
        }
        //both tables visible
        Assert.assertTrue(friendRequests);
        Assert.assertTrue(recommendedUsers);
    }

    @Test(groups = "contactsTab", priority = 40)
    public void goToContactsTab() throws Exception {

        tab_bar.findElement(By.name("Contacts")).click();
        Assert.assertTrue(tab_bar.findElement(By.name("Contacts")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        //when i tried to do assert by the staticText = "Chats" it failed because it says staticText = "Messages"
        //i don't understand how it says Messages and we see it Chats so i assert here by attribute:name
        Assert.assertEquals(nav_bar.getAttribute("name"),"Chats");
    }

    @Test(groups = "contactsTab",  priority = 41)
    public void loadContacts() throws Exception {
        if(!tab_bar.findElement(By.name("Contacts")).isSelected())
            tab_bar.findElement(By.name("Contacts")).click();
        //contacts tab shows only tableview and not the cells under it
        WebElement tableView = (WebElement)driver.findElement(By.xpath("//UIATableView"));
        //assert that the value matches
        Assert.assertTrue(tableView.getAttribute("value").contains("rows"));

    }

    @Test(groups = "notificationsTab", priority = 50)
    public void goToNotificationsTab() throws Exception {

        tab_bar.findElement(By.name("Notifications")).click();
        Assert.assertTrue(tab_bar.findElement(By.name("Notifications")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        Assert.assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(),"Notifications");
    }

    @Test(groups = "notificationsTab",  priority = 51)
    public void loadNotifications() throws Exception {
        if(!tab_bar.findElement(By.name("Notifications")).isSelected())
            tab_bar.findElement(By.name("Notifications")).click();

        List<MobileElement> tableCells = driver.findElements(By.xpath("//UIATableView/UIATableCell"));
        Assert.assertNotEquals(tableCells.size(), 0);
    }
}
