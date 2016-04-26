import java.net.MalformedURLException;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.annotations.*;
import org.testng.Assert.*;

import static org.testng.Assert.*;
import static org.testng.Assert.assertTrue;

/**
 * Created by imobarak on 3/31/16.
 */
public class PTTest {
    IOSDriver driver;
    DesiredCapabilities capabilities;
    WebElement nav_bar;
    WebElement tab_bar;
    String postDate;
    Boolean isNotificationSuccess;
    String adminUsername = "imobarak3";
    String nonAdminUsername = "name";
    String password = "sky";
    String signUpTestUser;
    //should have @Before @After if tests are independent and i'd want
    // to execute certain actions before/after every test
    //but in our case they depend on login to continue
   // @BeforeGroups("allTests")
    @BeforeTest(groups = "main")
    public void setup() throws MalformedURLException {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName","myphone");
        driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);
       try{
                  //accepting push notifications
           WebDriverWait wait = new WebDriverWait(driver, 15);
           wait.until(ExpectedConditions.alertIsPresent());
           Alert errorDialog = driver.switchTo().alert();
           errorDialog.accept();
       }catch (Exception e){
           //did not handle the ios notification
       }

        System.out.println("setup done");
    }

    //@AfterTest(groups = "allTests", alwaysRun = true )
    @AfterTest(groups = "main", alwaysRun = true )
    public void teardown(){
        driver.quit();
        System.out.println("clean up done");
    }

    @Test(groups = "logout", priority = 80)
    public  void logout() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        tab_bar = driver.findElementByClassName("UIATabBar");
        tab_bar.findElement(By.name("Newsfeed")).click();

        nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.name("username")).click();

        driver.findElement(By.name("Settings")).click();
        ((IOSElement) driver.findElementByClassName("UIATableView")).scrollTo("Log Out").click();
        List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
        Boolean logoutFound = false;
        for(IOSElement actionCell : actionCells) {
            try {
                actionCell.findElementByName("Log Out").click();
                logoutFound = true;
                try {
                    //handling alert
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert errorDialog = driver.switchTo().alert();
                    errorDialog.accept();
                    fail("Could not leave group alert is: " + errorDialog.getText());
                } catch (Exception e) {
                    //no alerts = worked as expected
                }
            }catch (Exception e){
                //not correct cell
            }
        }
        assertEquals(driver.findElementByClassName("UIANavigationBar").findElement(By.className("UIAStaticText")).getText(), "Login");

    }

    @Test(groups = "main", priority = 1)
    public  void signIn() throws Exception {
        WebElement element = driver.findElement(By.name("Sign In"));
        assertNotNull(element);
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
        String errorString = errorDialog.getText();
        errorDialog.accept();
        assertEquals(errorString, "Please provide a valid E-mail Address");
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
        String errorString = errorDialog.getText();
        errorDialog.accept();
        assertEquals(errorString, "We'll send you an e-mail with your username and new password.");
    }

    @Test(groups = "signup", priority=70)
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


        //page 1/3
        try {
            IOSElement table = (IOSElement) driver.findElementByClassName("UIATableView");
            List<MobileElement> rows = table.findElementsByClassName("UIATableCell");
            postDate = new SimpleDateFormat("YYMMdd_hhmm").format(new Date());
            signUpTestUser = "appiumTestUser" + postDate;
            rows.get(0).findElementByClassName("UIATextField").sendKeys(signUpTestUser + "@gmail.com");
            rows.get(1).findElementByClassName("UIATextField").sendKeys(signUpTestUser);
            System.out.println("Username signup test: " + signUpTestUser);
            rows.get(2).findElementByClassName("UIASecureTextField").sendKeys("password");
            rows.get(3).findElementByClassName("UIASecureTextField").sendKeys("password");
            nav_bar.findElement(By.name("NEXT")).click();
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("2/3")));
        }catch (Exception e){
            fail("Signup failed in step 1/3 issue is: " + e.getMessage());
        }
        //page 2/3
        try {
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            IOSElement table = (IOSElement) driver.findElementByClassName("UIATableView");
            List<MobileElement> rows = table.findElementsByClassName("UIATableCell");
            rows.get(1).findElementByClassName("UIATextView").sendKeys("I am the automated testing user");
            nav_bar.findElement(By.name("NEXT")).click();
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("3/3")));
        }catch (Exception e){
            fail("Signup failed in step 2/3 issue is: " + e.getMessage());
        }
        //page 3/3
        try {
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            IOSElement table = (IOSElement) driver.findElementByClassName("UIATableView");
            table.scrollTo("self harm").click();
            try {
                table.scrollTo("Appium_Public").click();

            }catch (Exception e){
                System.out.println("Could not join Appium_Public_Group but on self harm group");
            }
            try {
                table.scrollTo("Appium_Private_TestGroup").click();

            }catch (Exception e){
                System.out.println("Could not join Appium_Private_TestGroup but on self harm group");
            }

            nav_bar.findElement(By.name("DONE")).click();
        }catch (Exception e){
            fail("Signup failed in step 3/3 issue is: " + e.getMessage());
        }

        //check that it went to newsfeed
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(), "Newsfeed");
        assertTrue(driver.findElementByName("cancel").isDisplayed(), "Arrow and cancel button were not visible");
        driver.findElementByName("cancel").click();
        assertNotEquals(driver.findElementsByXPath("//UIATableView[1]/UIATableCell").size(), 0);
    }

    @Test(groups = "loginIssues", priority=4)
    public void loginWithInvalidCredentials() throws MalformedURLException {
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys("invalidUsername");
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).sendKeys("password");
        driver.findElement(By.name("SUBMIT")).click();

        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.alertIsPresent());

        Alert errorDialog = driver.switchTo().alert();
        String errorString = errorDialog.getText();
        errorDialog.accept();
        assertEquals(errorString, "Login failed. Username and/or Password incorrect");
        System.out.println("invalidLogin done");

        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).clear();
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).clear();
    }

    @Test(groups = "adminLogin", priority=2)
    public void loginAdmin() throws MalformedURLException {
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys(adminUsername);
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).sendKeys(password);
        driver.findElement(By.name("SUBMIT")).click();
        MobileElement successView = (MobileElement)new WebDriverWait(driver,15).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//UIANavigationBar/UIAStaticText")));
        System.out.println("validlogin done");
        tab_bar = driver.findElementByClassName("UIATabBar");

    }
    @Test(groups = "nonadminLogin", priority=3)
    public void loginNonAdmin() throws MalformedURLException {
        driver.findElement(By.xpath("//UIATableCell[1]/UIATextField")).sendKeys(nonAdminUsername);
        driver.findElement(By.xpath("//UIATableCell[2]/UIASecureTextField")).sendKeys(password);
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
        assertTrue(tab_bar.findElement(By.name("Newsfeed")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(), "Newsfeed");

    }

    @Test(groups = "newsfeed", dependsOnMethods = {"goToNewsFeedTab"}, priority = 11)
    public void loadNewsfeed() throws Exception {
        //first view in UICatalog is a table
        IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
        assertNotNull(table);
        //is number of cells/rows inside table correct
        List<MobileElement> rows = table.findElementsByClassName("UIATableCell");
        assertEquals(10, rows.size());
        //is the username loaded correctly for now i just check if not empty
        assertNotSame("", rows.get(0).findElement(By.className("UIAStaticText")).getText());
        System.out.println("loadNewsfeed");

    }

    @Test(groups = "newsfeed", dependsOnMethods = {"goToNewsFeedTab"}, priority=12)
    public void makePost() throws Exception {
        nav_bar.findElement(By.name("Post")).click();
        postDate = new SimpleDateFormat("dd-MM-YY hh:mm").format(new Date());
        driver.findElementByClassName("UIATextView").sendKeys("Testing new post through Appium " + postDate );
        nav_bar.findElement(By.name("Post")).click();
        System.out.println("makepost");
        checkPostSuccessful();
    }

    public void checkPostSuccessful() throws Exception {
        //first view in UICatalog is a table
        IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
        assertNotNull(table);
        //is number of cells/rows inside table correct
        List<MobileElement> rows = table.findElementsByClassName("UIATableCell");

       //check that username is correct and message is correct
       WebElement element = rows.get(0).findElements(By.className("UIAStaticText")).get(0);
        assertEquals(adminUsername, element.getText());
        element = rows.get(0).findElements(By.className("UIAStaticText")).get(1);
        assertEquals("Testing new post through Appium " + postDate, element.getText());
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
        assertEquals(new Long(numOfHugsAfter), new Long(numOfHugsBefore + 1));
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
        rows.get(0).findElement(By.className("UIATextView")).sendKeys("Comment through Appium " + postDate);
        rows.get(0).findElement(By.className("UIAButton")).click();
        new WebDriverWait(driver,15);
        table = (IOSElement)driver.findElementsByClassName("UIATableView").get(0);
        rows = table.findElementsByClassName("UIATableCell");
        assertEquals(numOfCellsBefore + 1, rows.size());
        System.out.println("comment post");
    }

    @Test(groups = "groupsTab", priority = 20)
    public void goToGroupsTab() throws Exception {

        tab_bar.findElement(By.name("Groups")).click();
        assertTrue(tab_bar.findElement(By.name("Groups")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(), "Support Groups");
    }

    @Test(groups = "groupsTab",  priority = 21)
    public void loadGroups() throws Exception {
        List<MobileElement> tableGroups = driver.findElements(By.xpath("//UIATableView/UIATableGroup"));
        //assert that there are 3 sections
        assertEquals(tableGroups.size(), 3);

        //assert that this section exists
        WebElement supportGroups = driver.findElementByName("YOUR SUPPORT GROUPS");
        assertNotNull(supportGroups);

        //assert that there are groups under this section - not empty
        List<MobileElement> cells = driver.findElements(By.xpath("//UIATableCell[preceding-sibling::UIATableGroup[1]/@name = 'YOUR SUPPORT GROUPS']"));
       assertNotEquals(cells.size(), 0);

        //assert that this section exists
        supportGroups = driver.findElementByName("RECOMMENDED SUPPORT GROUPS");
        assertNotNull(supportGroups);

        //assert that there are groups under this section - not empty
        cells = driver.findElements(By.xpath("//UIATableCell[preceding-sibling::UIATableGroup[1]/@name = 'RECOMMENDED SUPPORT GROUPS']"));
        assertNotEquals(cells.size(), 0);

    }

    @Test(groups = "groupsTab", priority = 22, enabled = false)
    public void searchGroup() throws Exception {

        tab_bar.findElement(By.name("Groups")).click();
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        try{
            tableView.scrollTo("Join a Support Group").click();
        }catch (NotFoundException e){
            fail("Unable to start search group");
        }
        //nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.className("UIASearchBar")).sendKeys("a");
//TODO: add wait here in case the connection is slow
       if( driver.findElementsByXPath("//UIATableView/UIATableCell").size() > 0){
           ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo("anxiety").click();
           assertTrue(nav_bar.getAttribute("name").equals("Group"), "Segue to group");
       }else
           fail("Search term did not return any results");

    }

    @Test(groups = "groupsTab", priority = 23)
    public void addGroup() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        tab_bar.findElement(By.name("Groups")).click();
       IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        try{
            tableView.scrollTo("Start a Support Group").click();
        }catch (NotFoundException e){
            fail("Unable to start add group");
        }
        List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
        String dateNow = new SimpleDateFormat("dd-MM-YY hh:mm").format(new Date());
        tableCells.get(1).findElementByClassName("UIATextField").sendKeys("Appium Group " + dateNow);
        tableCells.get(2).findElementByClassName("UIATextView").sendKeys("Appium description");
        tableCells.get(3).findElementByClassName("UIATextView").sendKeys("Appium keywords");
        tableCells.get(4).findElementByClassName("UIASwitch").click();
        tableCells.get(5).findElementByName("Add").click();

        try{
            //handling alert
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert errorDialog = driver.switchTo().alert();
            String errorString = errorDialog.getText();
            errorDialog.accept();
            assertNotEquals(errorString.toLowerCase(), "sorry, this group name already exists.", "Group name already exists");
        }catch (Exception e){
            //no alerts
        }
        try{
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("Success!")));
            driver.findElementByName("No Thanks").click();
        }catch (Exception e){
            fail("Did not add group successfully");
        }
    }

    @Test(groups = "groupsTab", priority = 24, enabled = false)
    public void checkGroupDetails() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        tab_bar.findElement(By.name("Groups")).click();
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        try{
            tableView.scrollTo("Appium_Group").click();
        }catch (NotFoundException e){
            fail("Unable to find group");
        }

        List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
        try{
            String reviewsCount = tableCells.get(1).findElementByClassName("UIAStaticText").getText();
            if(reviewsCount.contains("Review")){
                System.out.println("Group has no reviews");
            }else{

                reviewsCount = reviewsCount.substring(reviewsCount.indexOf("(") + 1);
                reviewsCount = reviewsCount.substring(0, reviewsCount.indexOf(")"));

                System.out.println("Group has " + reviewsCount + "reviews");
            }

        }catch(Exception e){
            fail("Could not define group reviews");
        }

        assertTrue(tableCells.get(2).findElementByClassName("UIAStaticText").getText().contains("Appium_Group"), "Group name incorrect");
        assertTrue(tableCells.get(2).findElementByClassName("UIATextView").getText().contains("Appium description"), "Group description incorrect");
        //this is a private group and not helper so we should find the privateIcon and no helperModeIcon
       try{
           tableCells.get(2).findElementByName("privateIcon");
           System.out.println("Group is private");
       }catch (NoSuchElementException e){
           fail("Private icon not found this group should be private");
       }

        try{
            tableCells.get(2).findElementByName("helperMode");
            fail("Helper icon is found in this group although it should not be in helper mode");
        }catch (NoSuchElementException e){
            System.out.println("Group is not in helper mode");
        }
        List<IOSElement> tableGroups = driver.findElementsByXPath("//UIATableView/UIATableGroup");
       try {
           String usersCount = tableGroups.get(1).findElementByClassName("UIAStaticText").getText();

           usersCount = usersCount.substring(usersCount.indexOf("(") + 1);
           usersCount = usersCount.substring(0, usersCount.indexOf(")"));
           System.out.println("Group has " + usersCount + " users");
       }
       catch (Exception e){
           fail("Could not define group users count");
       }

        try {
            for(int i=4; i<=6; i++) {
                assertTrue(tableCells.get(i).findElementByClassName("UIASwitch").isEnabled(), "Switch for " + tableCells.get(i).findElementByClassName("UIAStaticText").getText() + " is not visible");
            }
        }catch (Exception e){
            fail("Could not define group alerts");
        }

        assertTrue(tableCells.get(8).findElementByClassName("UIAStaticText").getText().contains("View Group Wall"), "View Group Wall button not found");

    }

    //Assumes: user joined Appium_Public_Group, which is a public group and he is not the admin
    @Test(groups = "groupsTab", priority = 25, enabled = false)
    public void canLeaveJoinPublicGroup() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        tab_bar.findElement(By.name("Groups")).click();
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        try{
            tableView.scrollTo("Appium_Public").click();
        }catch (NotFoundException e){
            fail("Unable to find group");
        }


        //assertTrue(tableCells.get(8).findElementByClassName("UIAStaticText").toString().contains("View Group Wall"), "View Group Wall button not found");
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.name("more")).click();

        List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
        Boolean leaveGroupFound = false;
        for(IOSElement actionCell : actionCells){
            try{
                actionCell.findElementByName("Leave Group").click();
                leaveGroupFound = true;
                try{
                    //handling alert
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert errorDialog = driver.switchTo().alert();
                    errorDialog.accept();
                    fail("Could not leave group alert is: " + errorDialog.getText());
                }catch (Exception e){
                    //no alerts = worked as expected
                }
                try{
                    List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
                    tableCells.get(4).findElementByName("Join Group").click();
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    new WebDriverWait(driver,15).until(ExpectedConditions.presenceOfElementLocated(By.name("Success!")));
                    driver.findElementByName("No Thanks").click();
                }catch (Exception e){
                    fail("Did not join group successfully");
                }
                break;
            }catch (Exception e){
                 //not leave group action
            }
        }
        assertTrue(leaveGroupFound, "Could not leave group");
    }

    //Assumes: user joined Appium_Private_Group, which is a private group and he is not the admin
    @Test(groups = "groupsTab", priority = 26, enabled = false)
    public void canLeaveJoinPrivateGroup() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        tab_bar.findElement(By.name("Groups")).click();
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        try{
            tableView.scrollTo("Appium_Private").click();
        }catch (NotFoundException e){
            fail("Unable to find group");
        }


         nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.name("more")).click();

        List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
        Boolean leaveGroupFound = false;
        for(IOSElement actionCell : actionCells){
            try{
                actionCell.findElementByName("Leave Group").click();
                leaveGroupFound = true;
                try{
                    //handling alert
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert errorDialog = driver.switchTo().alert();
                    errorDialog.accept();
                    fail("Could not leave group alert is: " + errorDialog.getText());
                }catch (Exception e){
                    //no alerts = worked as expected
                }
                try{
                    List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
                    tableCells.get(4).findElementByName("Join Group").click();
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert alertDialog = driver.switchTo().alert();
                    String alertText = alertDialog.getText();
                    alertDialog.accept();
                    assertTrue(alertText.toLowerCase().contains("waiting for approval"), "Did not receive alert notifying user that he is pending approval \\n alert:" + alertText);
                    //we can also assert that button name changed to "Join Request Sent"
                }catch (Exception e){
                    fail("Did not join group successfully");
                }
                break;
            }catch (Exception e){
                //not leave group action
            }
        }
        assertTrue(leaveGroupFound, "Could not leave group");
    }

    //Assumes: user joined Appium_Private_Group, which is a private group and he is not the admin
    @Test(groups = "setup", priority = 80)
    public void joinPrivateGroup() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        tab_bar.findElement(By.name("Groups")).click();
        try {
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        } catch (Exception e) {

        }
        tab_bar.findElement(By.name("Groups")).click();

        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        try {
            tableView.scrollTo("Join a Support Group").click();
        } catch (NotFoundException e) {
            fail("Unable to start search group");
        }
        //nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.className("UIASearchBar")).sendKeys("Appium_Private_Group");
//TODO: add wait here in case the connection is slow
        if (driver.findElementsByXPath("//UIATableView/UIATableCell").size() > 0) {
            ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo("Appium_Private_Group").click();
            assertTrue(nav_bar.getAttribute("name").equals("Group"), "Segue to group");
        } else
            fail("Search term did not return any results");

         try{
                    List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
                    tableCells.get(4).findElementByName("Join Group").click();
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert alertDialog = driver.switchTo().alert();
                    String alertText = alertDialog.getText();
                    alertDialog.accept();
                    assertTrue(alertText.toLowerCase().contains("waiting for approval"), "Did not receive alert notifying user that he is pending approval \\n alert:" + alertText);
                    //we can also assert that button name changed to "Join Request Sent"
                }catch (Exception e){
                    fail("Did not join group successfully");
                }

        }


    @Test(groups = "adminPanel", priority = 60, enabled = false)
    public void canBlockUsers() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            if(!nav_bar.findElement(By.className("UIAStaticText")).getText().equals("Group"))
                nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        if(!nav_bar.findElement(By.className("UIAStaticText")).getText().equals("Group")) {
            tab_bar.findElement(By.name("Groups")).click();
            IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
            try {
                tableView.scrollTo("Appium_Public").click();
            } catch (NotFoundException e) {
                fail("Unable to find group");
            }
        }

        try{
            ((IOSElement)driver.findElementByXPath("//UIATableView//UIATableCell[4]")).click();
            List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
            if(tableCells.size() == 0)
                fail("user list is empty");
            else{
                try {
                    MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                    element.findElement(By.name("Block")).click();
                    try{
                        //handling alert
                        WebDriverWait wait = new WebDriverWait(driver, 15);
                        wait.until(ExpectedConditions.alertIsPresent());
                        Alert errorDialog = driver.switchTo().alert();
                        errorDialog.accept();
                        fail("Could not block user alert is: " + errorDialog.getText());
                    }catch (Exception e){
                        //no alerts = worked as expected
                    }
                    try{
                        ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                        fail("Block failed user is still in list");
                    }catch (Exception e){
                        //not found: worked as expected
                    }
                }catch (NotFoundException e){
                    fail("User not found");
                }
            }
        }catch (Exception e){
            fail("Did not block user successfully: "+ e.getLocalizedMessage());
        }
    }

    @Test(groups = "adminPanel", priority = 61, enabled = false)
    public void canBroadcastMessage() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        tab_bar.findElement(By.name("Groups")).click();
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        try{
            tableView.scrollTo("Appium_Public").click();
        }catch (NotFoundException e){
            fail("Unable to find group");
        }


        nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.name("more")).click();

        List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
        Boolean adminPanelFound = false;
        for(IOSElement actionCell : actionCells){
            try{
                actionCell.findElementByName("Admin Panel").click();
                adminPanelFound = true;
                try{
                    List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
                    tableCells.get(0).findElementByName("Message All Users").click();
                    postDate = new SimpleDateFormat("dd-MM-YY hh:mm").format(new Date());
                    driver.findElementByClassName("UIATextView").sendKeys("Group broadcast through Appium " + postDate);
                    nav_bar.findElement(By.name("Post")).click();

                    //first view in UICatalog is a table
                    IOSElement table = (IOSElement)driver.findElementByClassName("UIATableView");
                    List<MobileElement> rows = table.findElementsByClassName("UIATableCell");

                    //check that username is correct and message is correct
                    WebElement element = rows.get(0).findElements(By.className("UIAStaticText")).get(0);
                    assertEquals("Appium_Public_Group", element.getText());
                    element = rows.get(0).findElements(By.className("UIAStaticText")).get(1);
                    assertEquals("Group broadcast through Appium " + postDate, element.getText());
                    System.out.println("broadcast message successful");
                }catch (Exception e){
                    fail("Did not broadcast successfully: "+ e.getLocalizedMessage());
                }
                break;
            }catch (Exception e){
                //not admin panel action
            }
        }
        assertTrue(adminPanelFound, "Could not find 'admin panel' in actionsheet");
    }

    @Test(groups = "adminPanel", priority = 62, enabled = false)
    public void canUnblockUsers() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel"))
                nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel")) {
            tab_bar.findElement(By.name("Groups")).click();
            IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
            try {
                tableView.scrollTo("Appium_Public").click();
            } catch (NotFoundException e) {
                fail("Unable to find group");
            }

            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("more")).click();

            List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
            Boolean adminPanelFound = false;
            for (IOSElement actionCell : actionCells) {
                try {
                    actionCell.findElementByName("Admin Panel").click();
                    adminPanelFound = true;
                    break;
                } catch (Exception e) {
                    //not admin panel action
                }
            }
            assertTrue(adminPanelFound, "Could not find 'admin panel' in actionsheet");
        }

        try{
            ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo("Blocked Users").click();
            List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
            if(tableCells.size() == 0)
                fail("Blocked users is empty");
            else{
                MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                element.findElement(By.name("Unblock")).click();
                try{
                    //handling alert
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert errorDialog = driver.switchTo().alert();
                    errorDialog.accept();
                    fail("Could not unblock user alert is: " + errorDialog.getText());
                }catch (Exception e){
                    //no alerts = worked as expected
                }
                try{
                    ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                    fail("Unblock failed user is still in list");
                }catch (Exception e){
                    //not found: worked as expected
                }
            }
        }catch (Exception e){
            fail("Did not block user successfully: "+ e.getLocalizedMessage());
        }
    }

    @Test(groups = "adminPanel", priority = 63, enabled = false)
    public void canAddRemoveHelper() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel"))
                nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel")) {
            tab_bar.findElement(By.name("Groups")).click();
            IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
            try {
                tableView.scrollTo("Appium_Public").click();
            } catch (NotFoundException e) {
                fail("Unable to find group");
                throw e;
            }

            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("more")).click();

            List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
            Boolean adminPanelFound = false;
            for (IOSElement actionCell : actionCells) {
                try {
                    actionCell.findElementByName("Admin Panel").click();
                    adminPanelFound = true;
                    break;
                } catch (Exception e) {
                    //not admin panel action
                }
            }
            assertTrue(adminPanelFound, "Could not find 'admin panel' in actionsheet");
        }

        try{
            ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo("Group Admins").click();
            List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
            if(tableCells.size() == 0)
                fail("members list is empty");
            else{
                try{
                    MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                    element.findElement(By.name("Add Helper")).click();
                    try{
                        //handling alert
                        WebDriverWait wait = new WebDriverWait(driver, 15);
                        wait.until(ExpectedConditions.alertIsPresent());
                        Alert errorDialog = driver.switchTo().alert();
                        errorDialog.accept();
                        fail("Could not add helper alert is: " + errorDialog.getText());
                    }catch (Exception e){
                        //no alerts = worked as expected
                    }
                }catch (Exception e){
                    fail("Could not add helper. " + e.getLocalizedMessage());
                }

                try{
                    MobileElement element = ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                    element.findElement(By.name("Remove Helper")).click();
                    try{
                        //handling alert
                        WebDriverWait wait = new WebDriverWait(driver, 15);
                        wait.until(ExpectedConditions.alertIsPresent());
                        Alert errorDialog = driver.switchTo().alert();
                        errorDialog.accept();
                        fail("Could not add helper alert is: " + errorDialog.getText());
                    }catch (Exception e){
                        //no alerts = worked as expected
                    }
                    try{
                        element = ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                        element.findElement(By.name("Add Helper"));
                    }catch (Exception e){
                        fail("could not verify user is not helper after clicking 'remove helper'" + e.getLocalizedMessage());
                    }

                }catch (Exception e){
                    fail("could not remove helper" + e.getLocalizedMessage());
                }
            }
        }catch (Exception e){
            fail("Did not block user successfully: "+ e.getLocalizedMessage());
        }
    }

        @Test(groups = "adminPanel", priority = 64, enabled = false)
        public void canTogglePrivateSwitch() throws Exception {
            try{
                nav_bar = driver.findElementByClassName("UIANavigationBar");
                if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel"))
                    nav_bar.findElement(By.name("Back")).click();
            }catch (Exception e){

            }
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel")) {
                tab_bar.findElement(By.name("Groups")).click();
                IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
                try {
                    tableView.scrollTo("Appium_Public").click();
                } catch (NotFoundException e) {
                    fail("Unable to find group");
                    throw e;
                }

                nav_bar = driver.findElementByClassName("UIANavigationBar");
                nav_bar.findElement(By.name("more")).click();

                List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
                Boolean adminPanelFound = false;
                for (IOSElement actionCell : actionCells) {
                    try {
                        actionCell.findElementByName("Admin Panel").click();
                        adminPanelFound = true;
                        break;
                    } catch (Exception e) {
                        //not admin panel action
                    }
                }
                assertTrue(adminPanelFound, "Could not find 'admin panel' in actionsheet");
            }

            try{
                MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo("Make Group Private");
                element.findElementByClassName("UIASwitch").click();
                        try{
                            //handling alert
                            WebDriverWait wait = new WebDriverWait(driver, 15);
                            wait.until(ExpectedConditions.alertIsPresent());
                            Alert errorDialog = driver.switchTo().alert();
                            errorDialog.accept();
                            fail("Could not make group private alert is: " + errorDialog.getText());
                        }catch (Exception e){
                            //no alerts = worked as expected
                        }
                    }catch (Exception e){
                        fail("Could click on make group private switch. " + e.getLocalizedMessage());
                    }

                    try{
                        ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo("Pending Requests");
                    }catch (Exception e){
                        fail("Group not set to private after pressing the switch: pending requests cell is not found");
                    }
            try {
                MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo("Make Group Private");
                element.findElementByClassName("UIASwitch").click();
                try{
                    //handling alert
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert errorDialog = driver.switchTo().alert();
                    errorDialog.accept();
                    fail("Could not make group private alert is: " + errorDialog.getText());
                }catch (Exception e){
                    //no alerts = worked as expected
                }
            }catch (Exception e){
                fail("Could click on make group private switch. " + e.getLocalizedMessage());
            }
            try{
                ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo("Pending Requests");
                fail("Group not set to public after pressing the switch: pending requests cell is found");
            }catch (Exception e){
                //not found = working as expected
            }
        }

    @Test(groups = "adminPanel", priority = 65, enabled = false)
    public void canToggleHelperSwitch() throws Exception {
        Integer actionCellIndex = 1;
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel"))
                nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel")) {
            tab_bar.findElement(By.name("Groups")).click();
            IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
            try {
                tableView.scrollTo("Appium_Public").click();
            } catch (NotFoundException e) {
                fail("Unable to find group");
                throw e;
            }

            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("more")).click();

            List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
            Boolean adminPanelFound = false;
            for (IOSElement actionCell : actionCells) {
                try {
                    actionCell.findElementByName("Admin Panel").click();
                    adminPanelFound = true;
                    break;
                } catch (Exception e) {
                    //not admin panel action
                    actionCellIndex++;
                }
            }
            assertTrue(adminPanelFound, "Could not find 'admin panel' in actionsheet");
        }

        try{
            MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo("Helper-Mode");
            element.findElementByClassName("UIASwitch").click();
            try{
                //handling alert
                WebDriverWait wait = new WebDriverWait(driver, 15);
                wait.until(ExpectedConditions.alertIsPresent());
                Alert errorDialog = driver.switchTo().alert();
                errorDialog.accept();
                fail("Could not make group in helper mode alert is: " + errorDialog.getText());
                return;
            }catch (Exception e){
                //no alerts = worked as expected
            }
        }catch (Exception e){
            fail("Could click on helper mode switch. " + e.getLocalizedMessage());
            throw e;
        }

        nav_bar.findElement(By.name("Back")).click();
        try{
            List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
            tableCells.get(2).findElementByName("helperMode");
            System.out.println("Group is in helper mode");
        }catch (NoSuchElementException e){
            fail("Helper icon is not found");
            throw e;
        }

        try {
            nav_bar.findElement(By.name("more")).click();
            //((IOSElement)driver.findElementByXPath("//UIAActionSheet/UIACollectionView[" + actionCellIndex + "]")).click();
            List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
            for (IOSElement actionCell : actionCells) {
                try {
                    actionCell.findElementByName("Admin Panel").click();
                    break;
                } catch (Exception e) {
                    //not admin panel action
                }
            }
            MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo("Helper-Mode");
            element.findElementByClassName("UIASwitch").click();
            try{
                //handling alert
                WebDriverWait wait = new WebDriverWait(driver, 15);
                wait.until(ExpectedConditions.alertIsPresent());
                Alert errorDialog = driver.switchTo().alert();
                errorDialog.accept();
                fail("Could not make group non-helper alert is: " + errorDialog.getText());
                return;
            }catch (Exception e){
                //no alerts = worked as expected
            }
        }catch (Exception e){
            fail("Could not click on helper-mode switch. " + e.getLocalizedMessage());
            throw e;
        }
        try{
            nav_bar.findElement(By.name("Back")).click();
            List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
            tableCells.get(2).findElementByName("helperMode");
            fail("Helper icon is found although we set it back to non-helper");
            return;
        }catch (NoSuchElementException e){
            System.out.println("Group is not in helper mode");
        }
    }

    //Assumes the signup test user joined Appium_Private_TestGroup successfully
    @Test(groups = "adminPanel", priority = 66)
    public void canAddPending() throws Exception {
        try{
            nav_bar = driver.findElementByClassName("UIANavigationBar");
            if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel"))
                nav_bar.findElement(By.name("Back")).click();
        }catch (Exception e){

        }
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        if(!nav_bar.findElement(By.className("UIAStaticText")).getText().contains("Admin Panel")) {
            tab_bar.findElement(By.name("Groups")).click();
            IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
            try {
                tableView.scrollTo("Appium_Private_Group").click();
            } catch (NotFoundException e) {
                fail("Unable to find group");
                throw e;
            }

            //ensure that this is now a private group through checking the private icon
            try{
                List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
                tableCells.get(2).findElementByName("privateIcon");
                System.out.println("Group is private");
            }catch (NoSuchElementException e){
                fail("Private icon not found this group should be private");
                throw e;
            }

            nav_bar = driver.findElementByClassName("UIANavigationBar");
            nav_bar.findElement(By.name("more")).click();

            List<IOSElement> actionCells = driver.findElementsByXPath("//UIAActionSheet/UIACollectionView");
            Boolean adminPanelFound = false;
            for (IOSElement actionCell : actionCells) {
                try {
                    actionCell.findElementByName("Admin Panel").click();
                    adminPanelFound = true;
                    break;
                } catch (Exception e) {
                    //not admin panel action
                }
            }
            assertTrue(adminPanelFound, "Could not find 'admin panel' in actionsheet");
        }

        try{
            ((IOSElement)driver.findElementByXPath("//UIATableView")).scrollTo("Pending Requests").click();
            List<IOSElement> tableCells = driver.findElementsByXPath("//UIATableView/UIATableCell");
            if(tableCells.size() == 0) {
                fail("pending list is empty");
            }
            else {
                try {
                    MobileElement element = ((IOSElement) driver.findElementByXPath("//UIATableView")).scrollTo(nonAdminUsername);
                    element.findElement(By.name("Accept")).click();
                    try {
                        //handling alert
                        WebDriverWait wait = new WebDriverWait(driver, 15);
                        wait.until(ExpectedConditions.alertIsPresent());
                        Alert errorDialog = driver.switchTo().alert();
                        errorDialog.accept();
                        fail("Could not add pending user alert is: " + errorDialog.getText());
                    } catch (Exception e) {
                        //no alerts = worked as expected
                    }
                } catch (Exception e) {
                    fail("Could not add pending user " + e.getLocalizedMessage());
                }

            }
        }catch (Exception e){
            fail("Did not add pending user successfully: "+ e.getLocalizedMessage());
        }
    }

    @Test(groups = "requestsTab", priority = 30)
    public void goToRequestsTab() throws Exception {

        tab_bar.findElement(By.name("Request")).click();
        assertTrue(tab_bar.findElement(By.name("Request")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(), "Friend Requests");
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
        assertTrue(friendRequests);
        assertTrue(recommendedUsers);
    }

    @Test(groups = "contactsTab", priority = 40)
    public void goToContactsTab() throws Exception {

        tab_bar.findElement(By.name("Contacts")).click();
        assertTrue(tab_bar.findElement(By.name("Contacts")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        //when i tried to do assert by the staticText = "Chats" it failed because it says staticText = "Messages"
        //i don't understand how it says Messages and we see it Chats so i assert here by attribute:name
        assertEquals(nav_bar.getAttribute("name"), "Chats");
    }

    @Test(groups = "contactsTab",  priority = 41)
    public void loadContacts() throws Exception {
        if(!tab_bar.findElement(By.name("Contacts")).isSelected())
            tab_bar.findElement(By.name("Contacts")).click();
        //contacts tab shows only tableview and not the cells under it
        WebElement tableView = (WebElement)driver.findElement(By.xpath("//UIATableView"));
        //assert that the value matches
        assertTrue(tableView.getAttribute("value").contains("rows"));

    }

    @Test(groups = "contactsTab",  priority = 42)
    public void startPrivateChat() throws Exception {
        if(!tab_bar.findElement(By.name("Contacts")).isSelected())
            tab_bar.findElement(By.name("Contacts")).click();
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.name("Add")).click();
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        assertTrue(tableView.getAttribute("value").contains("rows"));
        String chatToUser = "mob8";
        tableView.scrollTo(chatToUser).click();
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        assertTrue(nav_bar.getAttribute("name").equals(chatToUser));

        //assert that previous messages show correctly
        List<IOSElement> elements =  driver.findElementsByXPath("//UIACollectionView/UIACollectionCell");
        assertTrue(elements.size() > 0, "Messages loaded");

        MobileElement textView = (MobileElement) driver.findElementByXPath("//UIAToolbar/UIATextView");
        textView.clear();
        String date = new SimpleDateFormat("dd-MM-YY hh:mm").format(new Date());
        textView.sendKeys("Hi! Testing private chat through Appium " + date );
        driver.findElementByXPath("//UIAToolbar/UIAButton[2]").click();

        elements =  driver.findElementsByXPath("//UIACollectionView/UIACollectionCell");
        assertTrue(elements.size() > 0, "Messages loaded");
        Boolean stringVisible = false;
        Boolean sent = false;
        if(elements.get(elements.size()-1).getAttribute("name").contains("Hi! Testing private chat through Appium " + date)) {
            stringVisible = true;
        }

        elements =  driver.findElementsByXPath("//UIACollectionView/UIAStaticText");
        for (IOSElement element : elements) {
            if(element.getAttribute("name").contains("Sent")) {
                sent = true;
                break;
            }
        }
        assertTrue(stringVisible, "Message is visible");
        assertTrue(sent, "Message Sent successfully");
    }

    @Test(groups = "contactsTab",  priority = 43)
    public void startGroupChat() throws Exception {
       //force it to start from main contacts page
        tab_bar.findElement(By.name("Contacts")).click();
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.name("Add")).click();
        //nav_bar = driver.findElementByClassName("UIANavigationBar");
        nav_bar.findElement(By.name("Group Chat")).click();
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        assertTrue(tableView.getAttribute("value").contains("rows"));
        String[] groupChatMembers = new String[]{"imobaraktesting", "mob8"};
        for(String member : groupChatMembers){
            tableView.scrollTo(member).click();
        }
        nav_bar.findElement(By.name("Start")).click();
        assertTrue(nav_bar.getAttribute("name").contains(groupChatMembers[0]), "Group members added");
        assertTrue(nav_bar.getAttribute("name").contains(groupChatMembers[1]), "Group members added");
        MobileElement textView = (MobileElement) driver.findElementByXPath("//UIAToolbar/UIATextView");
        textView.clear();
        String date = new SimpleDateFormat("dd-MM-YY hh:mm").format(new Date());
        textView.sendKeys("Hi! Testing group chat through Appium " + date );
        driver.findElementByXPath("//UIAToolbar/UIAButton[2]").click();

        List<IOSElement> elements =  driver.findElementsByXPath("//UIACollectionView/UIACollectionCell");
        assertTrue(elements.size() > 0, "Messages loaded");
        Boolean stringVisible = false;
        Boolean sent = false;
        if(elements.get(elements.size()-1).getAttribute("name").contains("Hi! Testing group chat through Appium " + date)) {
            stringVisible = true;
        }

        elements =  driver.findElementsByXPath("//UIACollectionView/UIAStaticText");
        for (IOSElement element : elements) {
            if(element.getAttribute("name").contains("Sent")) {
                sent = true;
                break;
            }
        }
        assertTrue(stringVisible, "Message is visible");
        assertTrue(sent, "Message Sent successfully");
    }


    @Test(groups = "notificationsTab", priority = 50)
    public void goToNotificationsTab() throws Exception {

        tab_bar.findElement(By.name("Notifications")).click();
        assertTrue(tab_bar.findElement(By.name("Notifications")).isSelected());
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        assertEquals(nav_bar.findElement(By.className("UIAStaticText")).getText(), "Notifications");
    }

    @Test(groups = "notificationsTab",  priority = 51)
    public void loadNotifications() throws Exception {
        if(!tab_bar.findElement(By.name("Notifications")).isSelected())
            tab_bar.findElement(By.name("Notifications")).click();

        List<MobileElement> tableCells = driver.findElements(By.xpath("//UIATableView/UIATableCell"));
        assertNotEquals(tableCells.size(), 0);
    }

    @Test(groups = "notificationsTab",  priority = 52)
    public void browseFromNotifications() throws Exception {
        //force it to start from main notifications page
        tab_bar.findElement(By.name("Notifications")).click();
        nav_bar = driver.findElementByClassName("UIANavigationBar");
        IOSElement tableView = (IOSElement) driver.findElementByXPath("//UIATableView");
        assertTrue(tableView.getAttribute("value").contains("rows"));
        isNotificationSuccess = true;
        String[] searchTerms = { "hug", "commented", "profile","approval", "posted", "reviewed",  "congratulations", "assigned", "accepted" };
       for(String searchTerm : searchTerms) {
           scrollToNotification(searchTerm, tableView);
       }
        if(!isNotificationSuccess)
            fail("Did not browse to all notifications successfully, check logs for issues.");

    }

    private void outputInfo(String searchTerm, String expectedTitle, String navigationTitle, String scrollFrom){
        System.out.println("Did not browse to correct pending requests screen with search term: " + searchTerm);
        System.out.println("  --> Nav bar title of screen: " + navigationTitle);
        System.out.println("  --> Expected to be: " + expectedTitle);
        System.out.println("  --> Row record tested: " +scrollFrom);
        isNotificationSuccess = false;
    }
    private void scrollToNotification(String searchTerm, IOSElement tableView) {
        try {
            MobileElement element = tableView.scrollTo(searchTerm);
            String rowString = element.getAttribute("name");
            String[] words = rowString.split("\\s");
            element.click();
            switch (searchTerm.toLowerCase()){
                case "hug": case "commented": case "posted":
                    try
                    {
                        Alert errorDialog = driver.switchTo().alert();
                        System.out.println("Alert shown when browsing to post: "+ errorDialog.getText());
                        errorDialog.accept();
                    }
                    catch (NoAlertPresentException Ex)
                    {

                    }finally {
                        if(!nav_bar.getAttribute("name").equals("Post")) {
                            outputInfo(searchTerm,"Post", nav_bar.getAttribute("name"), rowString);
                        }
                        nav_bar.findElement(By.name("Back")).click();
                    }
                    break;
                case "profile": case "accepted":
                    //either profile or messages
                    if(!nav_bar.getAttribute("name").equals(words[0])) {
                        outputInfo(searchTerm, words[0], nav_bar.getAttribute("name"), rowString);
                    }
                    nav_bar.findElement(By.name("Back")).click();
                    break;
                case "approval":
                    if(!nav_bar.getAttribute("name").equals("Pending Requests")) {
                        outputInfo(searchTerm, "Pending Requests", nav_bar.getAttribute("name"), rowString);
                    }
                    nav_bar.findElement(By.name("Back")).click();
                    break;
                case "reviewed":
                    if(!nav_bar.getAttribute("name").equals("Reviews")) {
                        outputInfo(searchTerm, "Reviews", nav_bar.getAttribute("name"), rowString);
                    }
                    nav_bar.findElement(By.name("Back")).click();
                    break;
                case "congratulations":
                    if(!nav_bar.getAttribute("name").equals("My Profile")) {
                        outputInfo(searchTerm, "My Profile", nav_bar.getAttribute("name"), rowString);
                    }
                    nav_bar.findElement(By.name("Back")).click();
                    break;
                default:
                    break;
            }

        }catch (Exception e){
            System.out.println("Could not find notification with search term: " + searchTerm);
            isNotificationSuccess = false;
        }
    }
}
