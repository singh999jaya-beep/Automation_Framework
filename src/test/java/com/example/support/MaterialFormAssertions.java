package com.example.support;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Shared checks for Angular Material-style forms after an invalid submit attempt.
 */
public final class MaterialFormAssertions {

    private MaterialFormAssertions() {
    }

    /**
     * True if a visible validation hint exists or a modal/dialog is still open (typical after a blocked save).
     */
    public static boolean validationOrOpenDialogVisible(WebDriver driver) {
        pauseForUi();
        if (anyDisplayed(driver, By.cssSelector("mat-error"))) {
            return true;
        }
        if (anyDisplayed(driver, By.cssSelector(".mat-mdc-form-field-error"))) {
            return true;
        }
        if (anyDisplayed(driver, By.cssSelector(".mat-form-field-invalid mat-error"))) {
            return true;
        }
        if (anyDisplayed(driver, By.cssSelector("mat-dialog-container, .mat-mdc-dialog-container"))) {
            return true;
        }
        if (anyDisplayed(driver, By.cssSelector(".cdk-overlay-container [role='dialog']"))) {
            return true;
        }
        for (WebElement row : driver.findElements(By.cssSelector(".mat-mdc-snack-bar-label, .mat-snack-bar-container, mat-snack-bar-container"))) {
            try {
                if (row.isDisplayed()) {
                    String t = row.getText();
                    if (t != null && (t.toLowerCase().contains("error")
                            || t.toLowerCase().contains("invalid")
                            || t.toLowerCase().contains("required")
                            || t.toLowerCase().contains("fail"))) {
                        return true;
                    }
                }
            } catch (Exception ignored) {
                // stale
            }
        }
        return false;
    }

    private static void pauseForUi() {
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean anyDisplayed(WebDriver driver, By by) {
        for (WebElement el : driver.findElements(by)) {
            try {
                if (el.isDisplayed()) {
                    return true;
                }
            } catch (Exception ignored) {
                // stale
            }
        }
        return false;
    }
}
