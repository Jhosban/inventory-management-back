package edu.unac.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class InventorySteps {

    private ChromeDriver driver;

    @Before
    public void setUp(){
        System.setProperty("webdriver.chrome.driver",
                System.getProperty("user.dir") +
                        "/src/main/java/edu/unac/drivers/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary("C:\\Users\\super\\Downloads\\chrome-win64\\chrome-win64\\chrome.exe");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.get("http://localhost:5173/");
    }

    @After
    public void tearDown() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
    }

    //----------------------Crear item------------------------------------------

    @Given("an item is registered with name car, description car for events, and total quantity")
    public void an_item_is_registered_with_name_car_description_car_for_events_and_total_quantity() throws InterruptedException {
        WebElement addButtonDevice = driver.findElement(By.id("addButtonItem"));
        addButtonDevice.click();

        WebElement nameTextBox = driver.findElement(By.id("name"));
        WebElement descriptionTextBox = driver.findElement(By.id("description"));
        WebElement totalQuantityTextBox = driver.findElement(By.id("totalQuantity"));
        nameTextBox.sendKeys("car");
        descriptionTextBox.sendKeys("car for events");
        totalQuantityTextBox.sendKeys("5");

        WebElement createButtonDevice = driver.findElement(By.id("create-buttonItem"));
        createButtonDevice.click();

        Thread.sleep(1000);

        WebElement containerMessage = driver.findElement(By.id("swal2-html-container"));
        String message = containerMessage.getText();
        Assert.assertEquals("The item was created successfully", message,
                "The error message does not match what was expected.\n.");
        WebElement okButtonDevice = driver.findElement(By.xpath("/html/body/div[2]/div/div[6]/button[1]"));
        okButtonDevice.click();

    }

    //------------------------------Crear prestamo---------------------------

    @When("the user fills out the loan form with user Simon, item ID, quantity , start date, and end date")
    public void the_user_fills_out_the_loan_form_with_user_simon_item_id_quantity_start_date_and_end_date() throws InterruptedException {
        Thread.sleep(1000);

        WebElement addButtonDevice = driver.findElement(By.id("addButtonItemLoad"));
        addButtonDevice.click();

        WebElement itemIdTextBox = driver.findElement(By.id("itemId"));
        WebElement quantityTextBox = driver.findElement(By.id("quantity"));
        WebElement startDateTextBox = driver.findElement(By.id("startDate"));
        WebElement endDateTextBox = driver.findElement(By.id("endDate"));
        WebElement requestedByTextBox = driver.findElement(By.id("requestedBy"));

        itemIdTextBox.sendKeys("1");
        quantityTextBox.sendKeys("2");
        startDateTextBox.sendKeys("15/06/2025");
        endDateTextBox.sendKeys("20/06/2025");
        requestedByTextBox.sendKeys("Simon");

        WebElement createButtonDevice = driver.findElement(By.id("create-buttonItem"));
        createButtonDevice.click();

        Thread.sleep(1000);
    }
    @Then("the loan should be registered successfully")
    public void the_loan_should_be_registered_successfully() {
        WebElement containerMessage = driver.findElement(By.id("swal2-html-container"));
        String message = containerMessage.getText();
        Assert.assertEquals("The Load was created successfully", message,
                "The error message does not match what was expected.\n.");
        WebElement okButtonDevice = driver.findElement(By.xpath("/html/body/div[2]/div/div[6]/button[1]"));
        okButtonDevice.click();
    }

    @When("the user fills out the loan form again with user Simon, item ID , quantity  start date , and end date")
    public void the_user_fills_out_the_loan_form_again_with_user_simon_item_id_quantity_start_date_and_end_date() throws InterruptedException {

        WebElement addButtonDevice = driver.findElement(By.id("addButtonItemLoad"));
        addButtonDevice.click();

        WebElement itemIdTextBox = driver.findElement(By.id("itemId"));
        WebElement quantityTextBox = driver.findElement(By.id("quantity"));
        WebElement startDateTextBox = driver.findElement(By.id("startDate"));
        WebElement endDateTextBox = driver.findElement(By.id("endDate"));
        WebElement requestedByTextBox = driver.findElement(By.id("requestedBy"));

        itemIdTextBox.sendKeys("1");
        quantityTextBox.sendKeys("10");
        startDateTextBox.sendKeys("15/06/2025");
        endDateTextBox.sendKeys("20/06/2025");
        requestedByTextBox.sendKeys("Simon");

        WebElement createButtonDevice = driver.findElement(By.id("create-buttonItem"));
        createButtonDevice.click();

        Thread.sleep(1000);
    }
    @Then("the loan should not be registered")
    public void the_loan_should_not_be_registered() {
        WebElement containerMessage = driver.findElement(By.id("swal2-html-container"));
        String message = containerMessage.getText();
        Assert.assertEquals("Error Create Load", message,
                "The error message does not match what was expected.\n.");
    }

}