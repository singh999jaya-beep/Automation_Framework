package com.example.pages;

import com.example.pages.support.UserRowActionsConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class ManageEventsPage extends FlutterUsersTablePage {

    private static final String EVENTS_TEXT = "Events";
    private static final String MANAGE_EVENTS_TEXT = "Manage Events";
    private static final String NO_DATA_AVAILABLE_TEXT = "No data available";

    private static final String SEMANTICS_MENU = "events_submenu";
    private static final String SEMANTICS_SEARCH = "search_by_name__email_or_event_id_field";
    private static final String SEMANTICS_EVENT_ROW_NAME = "event_row_name_0";
    private static final String SEMANTICS_COPY_TEXT_BUTTON = "copy_text_button";
    private static final String SEMANTICS_DELETE = "delete_event_button_0";
    private static final String SEMANTICS_BLOCK = "block_event_button_0";
    private static final String SEMANTICS_CONFIRM_DELETE = "confirm_delete_button";
    private static final String SEMANTICS_CONFIRM_BLOCK = "confirm_block_button";

    private static final Duration ACTION_DIALOG_POLL = Duration.ofSeconds(12);

    private final By eventsMenu = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MENU + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_MENU + "')]");

    private final By eventNameCell = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_EVENT_ROW_NAME + "']" +
                    " | //flt-semantics[contains(@aria-label,'event_row_name')]");

    private final By searchBarSemantics = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]");

    private final By searchBarInput = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_SEARCH + "']//input" +
                    " | //input[@aria-label='" + SEMANTICS_SEARCH + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_SEARCH + "')]//input" +
                    " | //input[contains(@aria-label,'Search')]");

    private final By confirmDeleteButton = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_CONFIRM_DELETE + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_CONFIRM_DELETE + "')]");

    private final By confirmBlockButton = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_CONFIRM_BLOCK + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_CONFIRM_BLOCK + "')]");

    public ManageEventsPage(WebDriver driver) {
        super(driver, UserRowActionsConfig.events());
    }

    public void clickEventsMenu() {
        enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        openEventsSubmenu();
        waitUntil(this::isManageEventsPageLoaded, DEFAULT_WAIT);
        waitForSearchBarReady();
    }

    private void openEventsSubmenu() {
        enableFlutterSemantics();

        if (clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            pauseAfterNavigation();
            return;
        }
        if (!driver.findElements(eventsMenu).isEmpty()) {
            clickSemanticsElement(eventsMenu);
            pauseAfterNavigation();
            return;
        }

        if (!clickVisibleText(EVENTS_TEXT)) {
            throw new NoSuchElementException("Events menu item not found in navigation sidebar");
        }
        pauseAfterNavigation();

        if (clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            pauseAfterNavigation();
            return;
        }
        if (!driver.findElements(eventsMenu).isEmpty()) {
            clickSemanticsElement(eventsMenu);
            pauseAfterNavigation();
            return;
        }
        if (!clickVisibleText(MANAGE_EVENTS_TEXT)) {
            throw new NoSuchElementException(
                    "Manage Events submenu not found (expected semantics " + SEMANTICS_MENU + ")");
        }
        pauseAfterNavigation();
    }

    private void pauseAfterNavigation() {
        pauseAfterScroll();
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        String text = getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(EVENTS_TEXT)
                || text.contains("Navigation menu"));
    }

    public void clickSearchBar() {
        WebElement searchInput = waitForSearchInput();
        scrollIntoView(searchInput);
        searchInput.click();
    }

    public void enterSearchName(String name) {
        WebElement searchInput = waitForSearchInput();
        scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), name);
        searchInput.sendKeys(Keys.ENTER);
        waitUntil(d -> isSearchResultVisibleInRow(d, eventNameCell, name) || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public void enterSearchEventId(String eventId) {
        WebElement searchInput = waitForSearchInput();
        scrollIntoView(searchInput);
        searchInput.click();
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), eventId);
        searchInput.sendKeys(Keys.ENTER);
        waitUntil(d -> isEventSearchResultVisible(d, eventId) || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isNameDisplayed(String name) {
        return waitUntil(d -> isRowValueDisplayed(d, eventNameCell, name), DEFAULT_WAIT);
    }

    public boolean isEventIdDisplayed(String eventId) {
        return waitUntil(d -> isRowValueDisplayed(d, eventIdCellLocator(eventId), eventId), DEFAULT_WAIT);
    }

    public boolean isNoDataAvailable() {
        return waitUntil(this::isNoDataAvailableOnPage, DEFAULT_WAIT);
    }

    public boolean isNameDisplayedOrNoDataAvailable(String name) {
        return waitUntil(d -> isRowValueDisplayed(d, eventNameCell, name) || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public boolean isEventIdDisplayedOrNoDataAvailable(String eventId) {
        return waitUntil(d -> isRowValueDisplayed(d, eventIdCellLocator(eventId), eventId)
                || isNoDataAvailableOnPage(d), DEFAULT_WAIT);
    }

    public void clickDeleteEvent() {
        enableFlutterSemantics();
        waitForEventsTableReady();
        rowActions.scrollTableActionsIntoView();
        rowActions.clickDelete("Manage Events", this::scrollEventRowActionsIntoView);
    }

    public void clickBlockEvent() {
        enableFlutterSemantics();
        waitForEventsTableReady();
        rowActions.scrollTableActionsIntoView();
        rowActions.clickBlock("Manage Events", this::scrollEventRowActionsIntoView);
    }

    public void clickConfirmDeleteButton() {
        if (!waitUntil(this::isConfirmDeleteDialogOpen, ACTION_DIALOG_POLL)) {
            throw new NoSuchElementException(
                    "Delete confirmation dialog is not open (expected " + SEMANTICS_CONFIRM_DELETE + ")");
        }
        clickConfirmDialogAction(SEMANTICS_CONFIRM_DELETE, confirmDeleteButton, "Delete");
    }

    public void clickConfirmBlockButton() {
        if (!waitUntil(this::isConfirmBlockDialogOpen, ACTION_DIALOG_POLL)) {
            throw new NoSuchElementException(
                    "Block confirmation dialog is not open (expected " + SEMANTICS_CONFIRM_BLOCK + ")");
        }
        clickConfirmDialogAction(SEMANTICS_CONFIRM_BLOCK, confirmBlockButton, "Block");
    }

    private void scrollEventRowActionsIntoView() {
        enableFlutterSemantics();
        rowActions.scrollTableActionsIntoView();
        pauseAfterScroll();
    }

    private void waitForEventsTableReady() {
        waitUntil(d -> {
            enableFlutterSemanticsOn(d);
            return isManageEventsPageLoaded(d)
                    && (!d.findElements(eventNameCell).isEmpty()
                    || !d.findElements(By.xpath("//flt-semantics[@aria-label='" + SEMANTICS_DELETE + "']")).isEmpty()
                    || !d.findElements(By.xpath("//flt-semantics[@aria-label='" + SEMANTICS_BLOCK + "']")).isEmpty());
        }, DEFAULT_WAIT);
    }

    private void clickConfirmDialogAction(String semanticsLabel, By locator, String dialogButtonText) {
        if (clickSemanticsLabelViaScript(semanticsLabel)) {
            return;
        }
        enableFlutterSemantics();
        if (!driver.findElements(locator).isEmpty()) {
            clickSemanticsElement(locator);
            return;
        }
        if (clickVisibleText("Confirm") || clickVisibleText(dialogButtonText)) {
            return;
        }
        throw new NoSuchElementException("Confirm action is not clickable: " + semanticsLabel);
    }

    private boolean isConfirmDeleteDialogOpen(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(confirmDeleteButton).isEmpty()) {
            return true;
        }
        String pageText = getSemanticsText(webDriver);
        return pageText != null && pageText.toLowerCase().contains("are you sure you want to delete");
    }

    private boolean isConfirmBlockDialogOpen(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(confirmBlockButton).isEmpty()) {
            return true;
        }
        String pageText = getSemanticsText(webDriver);
        return pageText != null && pageText.toLowerCase().contains("are you sure you want to block");
    }

    private By eventIdCellLocator(String eventId) {
        return By.xpath(
                "(//flt-semantics[@aria-label='" + SEMANTICS_COPY_TEXT_BUTTON + "'])[1]" +
                        " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_COPY_TEXT_BUTTON + "')]" +
                        " | //flt-semantics[@aria-label='" + eventId + "']");
    }

    private boolean isEventSearchResultVisible(WebDriver webDriver, String eventId) {
        enableFlutterSemanticsOn(webDriver);
        if (isSearchResultVisibleInRow(webDriver, eventIdCellLocator(eventId), eventId)) {
            return true;
        }
        String text = normalizeText(getSemanticsText(webDriver));
        return text.contains(normalizeText(eventId));
    }

    private boolean isNoDataAvailableOnPage(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        String text = normalizeText(getSemanticsText(webDriver));
        return text != null && text.contains(normalizeText(NO_DATA_AVAILABLE_TEXT));
    }

    private boolean isRowValueDisplayed(WebDriver webDriver, By rowCellLocator, String searchText) {
        enableFlutterSemanticsOn(webDriver);
        String actual = normalizeText(getRowCellText(webDriver, rowCellLocator));
        String expected = normalizeText(searchText);
        String pageText = normalizeText(getSemanticsText(webDriver));
        if (actual.equals(expected) || actual.contains(expected)) {
            return true;
        }
        if (expected.matches("\\d+") && !webDriver.findElements(
                By.xpath("//flt-semantics[@aria-label='" + expected + "']")).isEmpty()) {
            return true;
        }
        if (pageText.contains(expected)) {
            return true;
        }
        String[] nameParts = expected.split(" ");
        if (nameParts.length > 1) {
            return pageText.contains(nameParts[0]) && pageText.contains(nameParts[nameParts.length - 1]);
        }
        return false;
    }

    private boolean isSearchResultVisibleInRow(WebDriver webDriver, By rowCellLocator, String searchText) {
        enableFlutterSemanticsOn(webDriver);
        String actual = normalizeText(getRowCellText(webDriver, rowCellLocator));
        if (actual.isEmpty()) {
            return false;
        }
        String expected = normalizeText(searchText);
        if (actual.equals(expected) || actual.contains(expected)) {
            return true;
        }
        String[] nameParts = expected.split(" ");
        if (nameParts.length > 1) {
            return actual.contains(nameParts[0]) && actual.contains(nameParts[nameParts.length - 1]);
        }
        return actual.contains(expected);
    }

    private String getRowCellText(WebDriver webDriver, By rowCellLocator) {
        if (webDriver.findElements(rowCellLocator).isEmpty()) {
            return "";
        }
        return getSemanticsNodeText(webDriver.findElement(rowCellLocator));
    }

    private boolean isManageEventsPageLoaded(WebDriver webDriver) {
        enableFlutterSemanticsOn(webDriver);
        if (!webDriver.findElements(searchBarSemantics).isEmpty()
                || !webDriver.findElements(searchBarInput).isEmpty()) {
            return true;
        }
        String text = getSemanticsText(webDriver);
        return text != null
                && text.contains(MANAGE_EVENTS_TEXT)
                && (text.contains("Event Name") || text.contains("Event ID"));
    }

    private WebElement waitForSearchInput() {
        waitForSearchBarReady();
        List<WebElement> inputs = driver.findElements(searchBarInput);
        if (!inputs.isEmpty()) {
            return inputs.get(0);
        }
        WebElement semantics = wait.until(ExpectedConditions.presenceOfElementLocated(searchBarSemantics));
        return semantics.findElement(By.xpath(".//input"));
    }

    private void waitForSearchBarReady() {
        waitUntil(d -> {
            enableFlutterSemanticsOn(d);
            return !d.findElements(searchBarSemantics).isEmpty()
                    || !d.findElements(searchBarInput).isEmpty();
        }, DEFAULT_WAIT);
    }
}
