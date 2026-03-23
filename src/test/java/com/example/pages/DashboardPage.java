package com.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.example.hooks.Hooks.driver;

public class DashboardPage {

    private final WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /* ---------- Locators ---------- */

    private final By dashboardHeader =
            By.xpath("//img[@alt='SafeTeam logo']");

    private final By teamFeedTextArea =
            By.xpath("//div[contains(@class,'angular-editor-textarea')]");

    private final By postToTeamFeed =
            By.xpath("(//div[@class='container'])[6]");

    private final By postButton =
            By.xpath("//span[normalize-space()='POST TO TEAM FEED']");



    private final By feedPost =
            By.xpath("(//div[contains(@class,'header')])[1]");



    private final By addLinkButton = By.xpath("//label[normalize-space()='Add link']");
    private final By addLinkInput = By.xpath("//input[@placeholder='Add your link here...']");

    /** File input for image upload - do not click the icon that opens the dialog; sendKeys(path) here instead. */


    private final By uploadImageFromPath = By.xpath("//label[normalize-space()='Add Images']");

    private final By imageFileInput = By.cssSelector("input[type='file'][accept*='image'], input[type='file']#imageUpload, #imageUpload");
    private  final By uploadVideoFromPath = By.xpath("//label[normalize-space()='Add Video']");

    /** File input for video upload - do not click the icon that opens the dialog; sendKeys(path) here instead. */
    private final By videoFileInput = By.cssSelector("#videoUpload, input[type='file'][accept*='video']");

    /* ---------- Edit post ---------- */
    private final By postThreeDotsMenu = By.xpath("(//*[name()='svg'][@role='img'])[11]");
    private final By editOption = By.xpath("//div[@class='cdk-overlay-container']//button[1]");
    /* Edit dialog text area: inside overlay (after clicking Edit), not a fixed index. */
    private final By editPostTextArea = By.xpath("//div[contains(@class,'cdk-overlay')]//div[contains(@class,'angular-editor-textarea')]");
    private final By saveChangesButton = By.xpath("//span[normalize-space()='SAVE CHANGES']");

    /* ---------- Delete post ---------- */
    private final By deleteOption = By.xpath("//app-menu-icons[normalize-space()='Delete']");
    private  final By deleteOptionVerify = By.xpath("//span[normalize-space()='Delete']");

    /* ---------- Comment on post ---------- */
    /* First post in feed, then the comments link (relative; avoids brittle full path and post[4]). */
    private final By commentsOfPost = By.xpath("//feed//post[1]//a[contains(.,'Comment') or contains(@aria-label,'comment') or contains(@class,'comment')]");
    /* Use [1] for first match; or any link containing the text (comment section may have one visible). */
    private final By beFirstToComment = By.xpath("//a[contains(.,'Be the first one to comment') or contains(.,'Be the first')]");
    private final By commentInput = By.xpath("(//textarea[@id='mainfield'])[1]");
    private final By postCommentButton = By.cssSelector(".svg-inline--fa.fa-paper-plane");

    /* ---------- Like / Dislike post ---------- */
    private final By likesIconOfPost = By.xpath("(//*[name()='path'][@fill='currentColor'])[12]");
    private final By likedState = By.xpath("//post[1]//div[1]//div[1]//div[3]//div[1]//div[1]//a[1]//p[1]");
    /* Like icon when post is already liked (click to dislike). */
    private final By alreadyLikedIconOfPost = By.xpath("(//*[name()='path'][@fill='currentColor'])[12]");
    private final By dislikedState = By.xpath("//post[1]//div[1]//div[1]//div[3]//div[1]//div[1]//a[1]//p[1]");

    /* ---------- Add Event ---------- */
    /* Plus: header add button (avoid ng-tns-* dynamic classes so selector stays valid across builds). */
    private final By plusIcon = By.cssSelector("div[class*='header-actions'] button[class*='add-btn']");
    /* Add Event: often in overlay menu; text may be in child (use contains). */
    private final By addEventOption = By.xpath("(//button[normalize-space()='Add Event'])[1]");
    /* Event form: target actual input/textarea elements (not wrapper divs) so clear/sendKeys work. Avoid dynamic ng-tns-* and mat-input-N ids. */
    private final By eventNameInput = By.xpath("(//div[contains(@class,'mat-form-field-infix')]//input)[1]");
    private final By eventLocationInput = By.xpath("/html[1]/body[1]/div[3]/div[2]/div[1]/mat-dialog-container[1]/app-event-details[1]/div[1]/form[1]/mat-form-field[2]/div[1]/div[1]/div[3]");
    private final By eventAddressInput = By.xpath("/html[1]/body[1]/div[3]/div[2]/div[1]/mat-dialog-container[1]/app-event-details[1]/div[1]/form[1]/mat-form-field[4]/div[1]/div[1]/div[3]");
    /* Element to scroll into view so location/address fields are visible. Match form by stable class (avoid ng-* state classes). */
    private final By eventTabOrForm = By.xpath("//form[contains(@class,'content')]");
    private final By saveButton = By.xpath("//span[normalize-space()='SAVE']");
    /* Event dialog form (inside overlay). Success = dialog closes; avoid dynamic ng-tns-* success message selector. */
    private  final  By createdEvent = By.xpath("(//div[@class='mat-menu-trigger cal-event ng-tns-c276-2 cal-draggable cal-event-detailed event ng-star-inserted'])[1]");
    private final By eventDialogForm = By.xpath("//div[contains(@class,'cdk-overlay')]//form[contains(@class,'content')]");
    /* Delete event: edit button on event card, then Delete button in event dialog. */
    private final By eventEditButton = By.xpath("//button[@class='mat-focus-indicator solid-primary mat-flat-button mat-button-base']");
    private final By eventDeleteButton = By.xpath("//button[@class='mat-focus-indicator mat-flat-button mat-button-base danger']");

    /* ---------- Add Announcement ---------- */
    private final By addAnnouncementOption = By.xpath("//button[normalize-space()='Add Announcement']");
    private final By announcementTitleInput = By.xpath("//textarea[@id='textarea']");
    private final By announcementDialogForm = By.xpath("//div[contains(@class,'cdk-overlay')]//form | //div[contains(@class,'cdk-overlay')]//mat-dialog-container");

    /* ---------- Delete Announcement ---------- */
    private final By announcementTab = By.xpath("//a[contains(.,'Announcement') or contains(.,'announcement')] | //button[contains(.,'Announcement')] | //*[contains(@class,'tab') and contains(.,'Announcement')]");
    private final By announcementDeleteButton = By.xpath("//button[contains(@class,'danger') or contains(.,'Delete')] | //*[contains(@aria-label,'delete') or contains(.,'delete')]");

    /* ---------- Add Game ---------- */
    private final By addGameOption = By.xpath("//button[normalize-space()='Add Game']");
    private final By gameOpponentNameInput = By.xpath("(//div[contains(@class,'mat-form-field-infix')]//input)[1]");
    private final By gameLocationInput = By.xpath("(//div[contains(@class,'mat-form-field-infix')]//input)[2]");
    private final By gameAddressInput = By.xpath("(//div[contains(@class,'mat-form-field-infix')]//input)[4]");
    private final By gameDialogForm = By.xpath("//div[contains(@class,'cdk-overlay')]//form | //div[contains(@class,'cdk-overlay')]//mat-dialog-container");

    /* ---------- Actions ---------- */

    public void waitForDashboard() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader));
    }

    public void writePost(String text) {
        WebElement postBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(teamFeedTextArea)
        );
        postBox.clear();
        postBox.sendKeys(text);
    }


    public void scrollToPostToTeamFeed() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(postToTeamFeed));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
    }



    public void clickPostButton() {
        WebElement postBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(postButton)
        );
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", postBtn);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", postBtn);
    }

    public void addLink(String url) {
        driver.findElement(addLinkButton).click();
        driver.findElement(addLinkInput).sendKeys(url);
        driver.findElement(addLinkButton).click();
    }

    public void uploadImage(String absolutePath) {
        driver.findElement(uploadImageFromPath).click();
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(imageFileInput));
        fileInput.sendKeys(absolutePath);
    }

    public void uploadVideo(String absolutePath) {
        driver.findElement(uploadVideoFromPath).click();
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(videoFileInput));
        fileInput.sendKeys(absolutePath);
    }

    /**
     * Upload video by sending the file path to the video file input (bypasses OS file explorer).
     * Use an absolute path; the file must exist on the machine running the test.
     */


    public void verifyPostPublished() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(feedPost));
    }

    /* ---------- Edit latest post ---------- */

    public void clickThreeDotsOnPost() {
        WebElement menuBtn = wait.until(ExpectedConditions.elementToBeClickable(postThreeDotsMenu));
        clickViaScript(menuBtn);
    }

    public void clickEditOption() {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(editOption));
        editBtn.click();
    }

    public void editPostWithText(String text) {
        WebElement textArea = wait.until(ExpectedConditions.visibilityOfElementLocated(editPostTextArea));
        textArea.click();
        textArea.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
    }

    /** Scrolls Save Changes button into view and clicks it. */
    public void scrollToSaveChangesAndClick() {
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveChangesButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", saveBtn);
        clickViaScript(saveBtn);
    }

    public void clickSaveChangesButton() {
        scrollToSaveChangesAndClick();
    }

    /* ---------- Delete latest post ---------- */

    public void clickDeleteOption() {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(deleteOption));
        clickViaScript(deleteBtn);
    }

    public void verifyPostDeleted() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteOptionVerify));
    }

    /* ---------- Comment on latest post ---------- */

    public void clickCommentsOfPost() {
        WebElement comments = wait.until(ExpectedConditions.elementToBeClickable(commentsOfPost));
        clickViaScript(comments);
    }

    public void clickBeFirstToComment() {
        scrollDown(300);
        WebElement beFirst = wait.until(ExpectedConditions.visibilityOfElementLocated(beFirstToComment));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", beFirst);
        beFirst = wait.until(ExpectedConditions.elementToBeClickable(beFirstToComment));
        clickViaScript(beFirst);
    }

    /** Scrolls the page down by the given pixels (e.g. to bring comment section into view). */
    private void scrollDown(int pixels) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + pixels + ");");
    }

    public void enterComment(String text) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(commentInput));
        input.click();
        input.sendKeys(text);
    }

    public void clickPostCommentButton() {
        WebElement postBtn = wait.until(ExpectedConditions.elementToBeClickable(postCommentButton));
        clickViaScript(postBtn);
    }

    public void verifyCommentPublished() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(beFirstToComment));
    }

    /* ---------- Like latest post ---------- */

    public void clickLikesIconOfPost() {
        WebElement likeIcon = wait.until(ExpectedConditions.elementToBeClickable(likesIconOfPost));
        clickViaScript(likeIcon);
    }

    public void verifyPostLiked() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(likedState));
    }

    /* ---------- Dislike latest post (click already-liked icon) ---------- */

    public void clickAlreadyLikedIconOfPost() {
        WebElement alreadyLiked = wait.until(ExpectedConditions.elementToBeClickable(alreadyLikedIconOfPost));
        clickViaScript(alreadyLiked);
    }

    public void verifyPostDisliked() {
        /* Same icon stays in DOM; wait for it to be clickable again (unliked state settled). */
        wait.until(ExpectedConditions.elementToBeClickable(likesIconOfPost));
    }

    /* ---------- Add Event ---------- */

    public void clickPlusIconAndSelectAddEvent() {
        WebElement plus = wait.until(ExpectedConditions.presenceOfElementLocated(plusIcon));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", plus);
        wait.until(ExpectedConditions.elementToBeClickable(plus));
        plus.click();
        WebElement addEvent = wait.until(ExpectedConditions.presenceOfElementLocated(addEventOption));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", addEvent);
        clickViaScript(addEvent);
    }

    public void enterEventName(String name) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(eventNameInput));
        input.clear();
        input.sendKeys(name);
    }

    public void scrollCurrentEventTab() {
        WebElement tab = wait.until(ExpectedConditions.presenceOfElementLocated(eventTabOrForm));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", tab);
    }

    public void enterEventLocation(String location) {
        WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(eventLocationInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", container);
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(container.findElement(By.xpath(".//input"))));
        input.clear();
        input.sendKeys(location);
    }

    public void enterEventAddress(String address) {
        WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(eventAddressInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", container);
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(container.findElement(By.xpath(".//input"))));
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), address);
    }

    public void clickSaveButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        clickViaScript(btn);
    }

    public void verifyEventCreated() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(eventDialogForm));
    }

    /* ---------- Delete Event ---------- */

    public void clickOnEvent() {
        WebElement clickEvent = wait.until(ExpectedConditions.elementToBeClickable(createdEvent));
        clickViaScript(clickEvent);
    }



    public void clickEditButtonOfEvent() {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(eventEditButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", editBtn);
        clickViaScript(editBtn);
    }

    public void clickEventDeleteButton() {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(eventDeleteButton));
        clickViaScript(deleteBtn);
    }

    public void verifyEventDeleted() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(eventDialogForm));
    }

    /* ---------- Add Announcement ---------- */

    public void clickPlusIconAndSelectAddAnnouncement() {
        WebElement plus = wait.until(ExpectedConditions.presenceOfElementLocated(plusIcon));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", plus);
        wait.until(ExpectedConditions.elementToBeClickable(plus));
        plus.click();

        WebElement addAnnouncement = wait.until(ExpectedConditions.presenceOfElementLocated(addAnnouncementOption));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", addAnnouncement);
        clickViaScript(addAnnouncement);
    }


    public void enterAnnouncementTitle(String title) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(announcementTitleInput));
        input.clear();
        input.sendKeys(title);
    }

    public void clickAddAnnouncementButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        clickViaScript(btn);
    }

    public void verifyAnnouncementCreated() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(announcementDialogForm));
    }

    /* ---------- Delete Announcement ---------- */

    public void clickAnnouncementTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(announcementTab));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", tab);
        clickViaScript(tab);
    }

    public void clickDeleteButtonOfAnnouncement() {
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(announcementDeleteButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", deleteBtn);
        clickViaScript(deleteBtn);
    }

    public void verifyAnnouncementDeleted() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(announcementDialogForm));
    }

    /* ---------- Add Game ---------- */

    public void clickPlusIconAndSelectAddGame() {
        WebElement plus = wait.until(ExpectedConditions.presenceOfElementLocated(plusIcon));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", plus);
        wait.until(ExpectedConditions.elementToBeClickable(plus));
        plus.click();
        WebElement addGame = wait.until(ExpectedConditions.presenceOfElementLocated(addGameOption));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", addGame);
        clickViaScript(addGame);
    }

    public void enterOpponentName(String name) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(gameOpponentNameInput));
        input.clear();
        input.sendKeys(name);
    }

    public void enterGameLocation(String location) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(gameLocationInput));
        input.clear();
        input.sendKeys(location);
    }



    public void enterGameAddress(String address) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(gameAddressInput));
        input.clear();
        input.sendKeys(address);
    }

    public void verifyGameAdded() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(gameDialogForm));
    }

    /** Clicks via dispatched MouseEvent so it works on SVG and other elements that may not have .click() in executeScript. */
    private void clickViaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));",
                element);
    }
}
