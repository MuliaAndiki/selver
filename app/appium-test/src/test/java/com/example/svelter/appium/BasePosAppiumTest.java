package com.example.svelter.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

public abstract class BasePosAppiumTest {

    protected static AndroidDriver driver;
    protected static WebDriverWait wait;

    @BeforeAll
    static void setUpDriver() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(System.getProperty("appium.device.name", "Android Emulator"))
                .setAppPackage(AppLocators.APP_PACKAGE)
                .setAppActivity(AppLocators.APP_ACTIVITY)
                .setAutoGrantPermissions(true)
                .setNoReset(false);

        String udid = System.getProperty("appium.device.udid");
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        String appPath = System.getProperty("appium.app.path");
        if (appPath != null && new File(appPath).exists()) {
            options.setApp(appPath);
        }

        String serverUrl = System.getProperty("appium.server.url", "http://127.0.0.1:4723");
        driver = new AndroidDriver(new URL(serverUrl), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @BeforeEach
    void resetAppToMainScreen() {
        driver.terminateApp(AppLocators.APP_PACKAGE);
        driver.activateApp(AppLocators.APP_PACKAGE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppLocators.TOOLBAR_HEADER));
        wait.until(ExpectedConditions.visibilityOfElementLocated(AppLocators.BTN_HITUNG));
    }

    @AfterAll
    static void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void fillForm(String nama, String harga, String jumlah) {
        setText(AppLocators.ET_NAMA_PESANAN, nama);
        setText(AppLocators.ET_HARGA, harga);
        setText(AppLocators.ET_JUMLAH, jumlah);
        hideKeyboardIfVisible();
    }

    protected void setText(By locator, String value) {
        scrollTo(locator);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        String currentValue = element.getText();
        if (value.isEmpty() && (currentValue == null || currentValue.isBlank())) {
            return;
        }

        element.click();
        driver.executeScript("mobile: type", Map.of(
                "elementId", ((RemoteWebElement) element).getId(),
                "text", value,
                "replace", true
        ));
    }

    protected void tapHitung() {
        hideKeyboardIfVisible();
        scrollTo(AppLocators.BTN_HITUNG);
        wait.until(ExpectedConditions.elementToBeClickable(AppLocators.BTN_HITUNG)).click();
    }

    protected void scrollTo(By locator) {
        String resourceId = locator.toString().replace("By.id: ", "");
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().resourceId(\"" + resourceId + "\"));"
            ));
        } catch (Exception ignored) {
            // Elemen mungkin sudah terlihat tanpa scroll.
        }
    }

    protected void hideKeyboardIfVisible() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception ignored) {
            // Keyboard sudah tertutup atau tidak tersedia.
        }
    }

    protected String getReceiptText() {
        scrollTo(AppLocators.TV_HASIL_STRUK);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(AppLocators.TV_HASIL_STRUK))
                .getText();
    }

    protected boolean isCheckboxChecked(By locator) {
        WebElement checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return "true".equals(checkbox.getAttribute("checked"));
    }

    protected void setTakeawayChecked(boolean checked) {
        scrollTo(AppLocators.CB_TAKEAWAY);
        if (isCheckboxChecked(AppLocators.CB_TAKEAWAY) != checked) {
            wait.until(ExpectedConditions.elementToBeClickable(AppLocators.CB_TAKEAWAY)).click();
        }
    }

    protected String waitForSnackbarErrorText() {
        hideKeyboardIfVisible();

        By snackbarText = By.xpath(
                "//*[@resource-id='" + AppLocators.APP_PACKAGE + ":id/snackbar_error']" +
                        "//android.widget.TextView | " +
                        "//*[@content-desc='" + AppLocators.VALIDATION_ERROR_TEXT + "'] | " +
                        "//android.widget.TextView[contains(@text, '" + AppLocators.VALIDATION_ERROR_TEXT + "')]"
        );

        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(snackbarText));
        String text = message.getText();
        if (text != null && !text.isBlank()) {
            return text.trim();
        }

        String contentDescription = message.getAttribute("contentDescription");
        return contentDescription != null ? contentDescription.trim() : "";
    }
}
