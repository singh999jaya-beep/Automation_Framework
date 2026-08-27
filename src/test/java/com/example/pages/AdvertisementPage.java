package com.example.pages;

import com.example.pages.support.FlutterSemanticsSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class AdvertisementPage {

    private static final String ADVERTISEMENTS_TEXT = "Advertisements";
    private static final String CREATE_ADS_TEXT = "Create Ads";
    private static final String MY_ADS_TEXT = "My Ads";
    private static final String PUBLISH_TEXT = "Publish";

    private static final String SEMANTICS_MENU = "advertisement_submenu";
    private static final String SEMANTICS_CREATE_AD = "create_ad_button";
    private static final String SEMANTICS_CAMPAIGN_NAME = "campaign_name_field";
    private static final String SEMANTICS_WEBSITE_URL = "website_url_field";
    private static final String SEMANTICS_AD_TITLE = "ad_title_input_field";
    private static final String SEMANTICS_AD_DESCRIPTION = "controllerstate_field";
    private static final String SEMANTICS_PUBLISH = "publish_button";
    private static final String SEMANTICS_UPLOAD_MEDIA = "upload_media_button";

    private static final String FALLBACK_CAMPAIGN_NAME = "Campaign Name";
    private static final String FALLBACK_WEBSITE_URL = "Add URL/Website";
    private static final String FALLBACK_AD_TITLE = "Title";
    private static final String FALLBACK_AD_DESCRIPTION = "Ad Description";
    private static final String FALLBACK_UPLOAD_HERE = "Upload here";
    private static final String AD_MEDIA_FILE_NAME = "carrer.jpg";

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    private static final Duration PUBLISH_WAIT = Duration.ofSeconds(30);
    private static final Duration PAGE_POLL = Duration.ofSeconds(10);
    private static final int MAX_ACTION_ATTEMPTS = 5;
    private static final int MAX_PUBLISH_ATTEMPTS = 10;
    private static final int MAX_NAV_ATTEMPTS = 10;

    private static final Keys SELECT_ALL_MODIFIER =
            System.getProperty("os.name", "").toLowerCase().contains("mac")
                    ? Keys.COMMAND
                    : Keys.CONTROL;

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FlutterSemanticsSupport semantics;

    private final By advertisementsMenu = By.xpath(
            "//flt-semantics[@aria-label='" + SEMANTICS_MENU + "']" +
                    " | //flt-semantics[contains(@aria-label,'" + SEMANTICS_MENU + "')]");
    private final By uploadMediaButton = semanticsButton(SEMANTICS_UPLOAD_MEDIA);

    public AdvertisementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);
        this.semantics = new FlutterSemanticsSupport(driver);
    }

    public void clickAdvertisementsMenu() {
        semantics.enableFlutterSemantics();
        waitUntil(this::isHomePageLoaded, DEFAULT_WAIT);
        openAdvertisementsSection();
        if (!waitUntil(this::isAdvertisementsPageLoaded, PAGE_POLL)) {
            throw new NoSuchElementException(
                    "Advertisements page is not loaded (expected '" + MY_ADS_TEXT + "' or '" + CREATE_ADS_TEXT + "')");
        }
    }

    public void clickCreateAdButton() {
        openCreateAdForm();
    }

    public void enterCampaignName(String campaignName) {
        openCreateAdForm();
        enterField(SEMANTICS_CAMPAIGN_NAME, FALLBACK_CAMPAIGN_NAME, campaignName);
    }

    public void enterWebsiteUrl(String url) {
        enterField(SEMANTICS_WEBSITE_URL, FALLBACK_WEBSITE_URL, url);
    }

    public void enterTitle(String title) {
        enterField(SEMANTICS_AD_TITLE, FALLBACK_AD_TITLE, title);
    }

    public void scrollDownPage() {
        semantics.enableFlutterSemantics();
        scrollFormDown();
    }

    public void scrollToUploadMedia() {
        semantics.enableFlutterSemantics();
        scrollToFieldLabel(SEMANTICS_UPLOAD_MEDIA, FALLBACK_UPLOAD_HERE);
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "let node = findOutsideNav('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]');" +
                        "if (!node) {" +
                        "  node = Array.from(document.querySelectorAll('flt-semantics')).find(candidate => {" +
                        "    if (isInNav(candidate)) return false;" +
                        "    const text = (candidate.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const label = candidate.getAttribute('aria-label') || '';" +
                        "    return text === wanted || text.includes('Upload Image') || label.includes('upload_media');" +
                        "  }) || null;" +
                        "}" +
                        "if (node) node.scrollIntoView({block: 'center', inline: 'nearest'});",
                FALLBACK_UPLOAD_HERE,
                SEMANTICS_UPLOAD_MEDIA
        );
        semantics.pauseAfterScroll();
    }

    public void enterAdDescription(String description) {
        semantics.enableFlutterSemantics();
        if (!waitUntil(this::isCreateAdFormLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException("Create ad form is not visible for Ad Description");
        }
        scrollToFieldLabel(FALLBACK_AD_DESCRIPTION, FALLBACK_AD_DESCRIPTION);
        WebElement input = findAdDescriptionInput();
        typeExactValue(input, description);
    }

    public void clickUploadHere() {
        clickUploadHere(null);
    }

    public void clickUploadHere(String mediaPath) {
        semantics.enableFlutterSemantics();
        String resolvedPath = resolveAdMediaPath(mediaPath);
        if (!waitUntil(this::isCreateAdFormLoaded, DEFAULT_WAIT)) {
            openCreateAdForm();
        }
        revealUploadSection();
        scrollToUploadMedia();
        if (!uploadMediaWithFileChooser(resolvedPath)) {
            throw new NoSuchElementException("Failed to attach media file: " + resolvedPath);
        }
        blurActiveElement();
        dismissBlockingUi();
        semantics.forceEnableFlutterSemantics();
        waitForPublishButtonAfterUpload();
        semantics.pauseAfterScroll();
    }

    private boolean clickUploadMediaControl() {
        semantics.enableFlutterSemantics();
        scrollToUploadMedia();
        waitUntil(this::isUploadMediaButtonSemanticsPresent, Duration.ofSeconds(10));

        // Primary path: exact Flutter semantics label upload_media_button.
        if (clickUploadMediaButtonSemanticsExact()) {
            return true;
        }
        boolean clicked = semantics.clickSemanticsLabelViaScript(SEMANTICS_UPLOAD_MEDIA);
        if (!clicked && !driver.findElements(uploadMediaButton).isEmpty()) {
            try {
                clickSemanticsElement(uploadMediaButton);
                clicked = true;
            } catch (Exception ignored) {
                clicked = false;
            }
        }
        if (!clicked) {
            clicked = clickUploadMediaViaScript();
        }
        if (!clicked) {
            clicked = semantics.clickVisibleText(FALLBACK_UPLOAD_HERE)
                    || semantics.clickVisibleText("Upload Image");
        }
        if (!clicked) {
            clicked = clickUploadAllowingZeroSize();
        }
        return clicked;
    }

    private boolean isUploadMediaButtonSemanticsPresent(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return !webDriver.findElements(By.cssSelector(
                "flt-semantics[aria-label='" + SEMANTICS_UPLOAD_MEDIA + "']"
        )).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return !!findOutsideNav('flt-semantics[aria-label=\"upload_media_button\"]');"
        ));
    }

    private boolean clickUploadMediaButtonSemanticsExact() {
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_UPLOAD_MEDIA)) {
            return true;
        }
        List<WebElement> nodes = driver.findElements(By.cssSelector(
                "flt-semantics[aria-label='" + SEMANTICS_UPLOAD_MEDIA + "']"
        ));
        for (WebElement node : nodes) {
            try {
                if (isInsideNavigation(node)) {
                    continue;
                }
                semantics.scrollIntoView(node);
                if (semantics.clickElementReliably(node)) {
                    return true;
                }
                if (Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                        "const node = arguments[0];" +
                                "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                                "target.scrollIntoView({block:'center', inline:'nearest'});" +
                                "try { target.click(); return true; } catch (e) { return false; }",
                        node
                ))) {
                    return true;
                }
            } catch (Exception ignored) {
                // Try next candidate.
            }
        }
        return clickUploadAllowingZeroSize();
    }

    private boolean clickUploadAllowingZeroSize() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const node = findOutsideNav('flt-semantics[aria-label=\"upload_media_button\"]');" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "target.scrollIntoView({block:'center', inline:'nearest'});" +
                        "if (dispatchPointerClick(target)) return true;" +
                        "try { target.click(); return true; } catch (e) {}" +
                        "const rect = target.getBoundingClientRect();" +
                        "const x = rect.left + Math.max(rect.width, 1) / 2;" +
                        "const y = rect.top + Math.max(rect.height, 1) / 2;" +
                        "const hit = document.elementFromPoint(x, y) || target;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});" +
                        "return true;"
        ));
    }

    private void revealUploadSection() {
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            scrollToUploadMedia();
            if (isUploadMediaPresent(driver)) {
                return;
            }
            scrollFormDown();
        }
        scrollToUploadMedia();
    }

    public void clickPublishButton() {
        semantics.enableFlutterSemantics();
        semantics.forceEnableFlutterSemantics();
        blurActiveElement();
        dismissBlockingUi();
        revealPublishSection();

        // Primary path: exact Flutter semantics label publish_button.
        if (!waitUntil(this::isPublishButtonSemanticsPresent, PUBLISH_WAIT)) {
            revealPublishSection();
            waitUntil(this::isPublishButtonSemanticsPresent, Duration.ofSeconds(10));
        }

        for (int attempt = 0; attempt < MAX_PUBLISH_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollFormDown();
                dismissBlockingUi();
                semantics.enableFlutterSemantics();
            }
            scrollToPublishAction();
            if (clickPublishButtonSemanticsExact()) {
                return;
            }
            if (clickPublishBySemanticsNode()) {
                return;
            }
            if (clickSemanticsAction(SEMANTICS_PUBLISH, PUBLISH_TEXT)) {
                return;
            }
            if (clickPublishViaScript()) {
                return;
            }
            if (clickPublishAllowingZeroSize()) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        throw new NoSuchElementException(
                "Publish button not found (expected semantics " + SEMANTICS_PUBLISH
                        + " or text '" + PUBLISH_TEXT + "'). "
                        + describePublishCandidates());
    }

    private boolean isPublishButtonSemanticsPresent(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return !webDriver.findElements(By.cssSelector(
                "flt-semantics[aria-label='" + SEMANTICS_PUBLISH + "']"
        )).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return !!findOutsideNav('flt-semantics[aria-label=\"publish_button\"]');"
        ));
    }

    private boolean clickPublishButtonSemanticsExact() {
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_PUBLISH)) {
            return true;
        }
        List<WebElement> nodes = driver.findElements(By.cssSelector(
                "flt-semantics[aria-label='" + SEMANTICS_PUBLISH + "']"
        ));
        for (WebElement node : nodes) {
            try {
                if (isInsideNavigation(node)) {
                    continue;
                }
                semantics.scrollIntoView(node);
                if (semantics.clickElementReliably(node)) {
                    return true;
                }
                return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                        "const node = arguments[0];" +
                                "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                                "target.scrollIntoView({block:'center', inline:'nearest'});" +
                                "try { target.click(); return true; } catch (e) { return false; }",
                        node
                ));
            } catch (Exception ignored) {
                // Try next candidate.
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const node = findOutsideNav('flt-semantics[aria-label=\"publish_button\"]');" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "if (dispatchPointerClick(target)) return true;" +
                        "try { target.click(); return true; } catch (e) { return false; }"
        ));
    }

    private void waitForPublishButtonAfterUpload() {
        for (int attempt = 0; attempt < MAX_PUBLISH_ATTEMPTS; attempt++) {
            if (isPublishActionPresent(driver)) {
                return;
            }
            dismissBlockingUi();
            semantics.forceEnableFlutterSemantics();
            scrollFormDown();
            scrollToPublishAction();
            if (waitUntil(this::isPublishActionPresent, Duration.ofSeconds(3))) {
                return;
            }
        }
        // Upload step still succeeds; publish click will surface a clearer failure.
    }

    private void dismissBlockingUi() {
        try {
            driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
        } catch (Exception ignored) {
            // Best-effort: close leftover native/file overlays.
        }
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "document.dispatchEvent(new KeyboardEvent('keydown', {key:'Escape', keyCode:27, bubbles:true}));" +
                            "document.dispatchEvent(new KeyboardEvent('keyup', {key:'Escape', keyCode:27, bubbles:true}));"
            );
        } catch (Exception ignored) {
            // Ignore JS keyboard fallback failures.
        }
        semantics.pauseAfterScroll();
    }

    private void revealPublishSection() {
        semantics.forceEnableFlutterSemantics();
        for (int attempt = 0; attempt < MAX_PUBLISH_ATTEMPTS; attempt++) {
            scrollToPublishAction();
            if (isPublishActionPresent(driver)) {
                return;
            }
            scrollFormDown();
            semantics.forceEnableFlutterSemantics();
        }
        scrollToPublishAction();
    }

    private void scrollToPublishAction() {
        semantics.forceEnableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "let node = findOutsideNav('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"publish\"]');" +
                        "if (!node) {" +
                        "  node = Array.from(document.querySelectorAll('flt-semantics')).find(candidate => {" +
                        "    if (isInNav(candidate)) return false;" +
                        "    const text = (candidate.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const label = (candidate.getAttribute('aria-label') || '').toLowerCase();" +
                        "    return text === wanted || label.includes('publish');" +
                        "  }) || null;" +
                        "}" +
                        "if (node) node.scrollIntoView({block: 'center', inline: 'nearest'});",
                PUBLISH_TEXT,
                SEMANTICS_PUBLISH
        );
        semantics.pauseAfterScroll();
    }

    private boolean clickPublishBySemanticsNode() {
        List<WebElement> nodes = driver.findElements(By.cssSelector(
                "flt-semantics[aria-label='" + SEMANTICS_PUBLISH + "'],"
                        + " flt-semantics[aria-label*='" + SEMANTICS_PUBLISH + "'],"
                        + " flt-semantics[aria-label*='publish']"
        ));
        for (WebElement node : nodes) {
            try {
                if (isInsideNavigation(node)) {
                    continue;
                }
                semantics.scrollIntoView(node);
                if (semantics.clickElementReliably(node)) {
                    return true;
                }
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", node);
                return true;
            } catch (Exception ignored) {
                // Try next candidate.
            }
        }
        return false;
    }

    private boolean isInsideNavigation(WebElement node) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return isInNav(arguments[0]);",
                node
        ));
    }

    private boolean clickPublishViaScript() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const semanticsLabel = arguments[1];" +
                        "const candidates = [" +
                        "  findOutsideNav('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')," +
                        "  findOutsideNav('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]')," +
                        "  findOutsideNav('flt-semantics[aria-label*=\"publish\"]')" +
                        "].concat(Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === wanted || label.includes('publish');" +
                        "}));" +
                        "for (const node of candidates) {" +
                        "  if (!node) continue;" +
                        "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "  if (dispatchPointerClick(target)) return true;" +
                        "  try { target.click(); return true; } catch (e) {}" +
                        "}" +
                        "return false;",
                PUBLISH_TEXT,
                SEMANTICS_PUBLISH
        ));
    }

    private boolean clickPublishAllowingZeroSize() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const semanticsLabel = arguments[0];" +
                        "const wanted = arguments[1];" +
                        "const node = findOutsideNav('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]')" +
                        "  || Array.from(document.querySelectorAll('flt-semantics')).find(candidate => {" +
                        "    if (isInNav(candidate)) return false;" +
                        "    const text = (candidate.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "    const label = (candidate.getAttribute('aria-label') || '').toLowerCase();" +
                        "    return text === wanted || label.includes('publish');" +
                        "  }) || null;" +
                        "if (!node) return false;" +
                        "const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "try { target.click(); return true; } catch (e) {}" +
                        "const rect = target.getBoundingClientRect();" +
                        "const x = rect.left + Math.max(rect.width, 1) / 2;" +
                        "const y = rect.top + Math.max(rect.height, 1) / 2;" +
                        "const hit = document.elementFromPoint(x, y) || target;" +
                        "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                        "  hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                        "});" +
                        "return true;",
                SEMANTICS_PUBLISH,
                PUBLISH_TEXT
        ));
    }

    private String describePublishCandidates() {
        Object detail = ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const host = document.querySelector('flt-semantics-host');" +
                        "const all = Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const outside = all.filter(node => !isInNav(node));" +
                        "const labels = outside.map(node => (node.getAttribute('aria-label') || '').trim())" +
                        "  .filter(label => label.length > 0).slice(0, 30);" +
                        "return {" +
                        "  hostExists: !!host," +
                        "  totalSemantics: all.length," +
                        "  outsideNav: outside.length," +
                        "  hostTextLength: ((host && host.textContent) || '').trim().length," +
                        "  labels: labels" +
                        "};"
        );
        return "Semantics snapshot: " + detail;
    }

    private void openAdvertisementsSection() {
        semantics.enableFlutterSemantics();
        if (isAdvertisementsPageLoaded(driver)) {
            return;
        }
        for (int attempt = 0; attempt < MAX_NAV_ATTEMPTS; attempt++) {
            scrollNavigationToAdvertisements();
            if (clickAdvertisementsNavItem()) {
                pauseAfterNavigation();
                if (isAdvertisementsPageLoaded(driver)) {
                    return;
                }
                if (waitUntil(this::isAdvertisementsPageLoaded, PAGE_POLL)) {
                    return;
                }
            }
            scrollNavigationSidebarDown();
            pauseAfterNavigation();
        }
        throw new NoSuchElementException(
                "Advertisements menu item not found in navigation sidebar (expected semantics "
                        + SEMANTICS_MENU + ")");
    }

    private boolean clickAdvertisementsNavItem() {
        if (semantics.clickSemanticsLabelViaScript(SEMANTICS_MENU)) {
            return true;
        }
        if (!driver.findElements(advertisementsMenu).isEmpty()) {
            try {
                clickSemanticsElement(advertisementsMenu);
                return true;
            } catch (Exception ignored) {
                // Continue to text/script fallbacks.
            }
        }
        if (semantics.clickVisibleText(ADVERTISEMENTS_TEXT)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                DISPATCH_POINTER_CLICK_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const nodes = menu ? Array.from(menu.querySelectorAll('flt-semantics'))" +
                        "  : Array.from(document.querySelectorAll('flt-semantics'));" +
                        "const target = nodes.find(node => {" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === wanted || label.includes('advertisement');" +
                        "});" +
                        "if (!target) return false;" +
                        "const tap = target.closest('flt-semantics[flt-tappable]') || target;" +
                        "return dispatchPointerClick(tap);",
                ADVERTISEMENTS_TEXT
        ));
    }

    private void scrollNavigationSidebarDown() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "const anchor = menu || document.body;" +
                        "for (let i = 0; i < 3; i++) {" +
                        "  anchor.dispatchEvent(new WheelEvent('wheel', {" +
                        "    deltaX: 0, deltaY: 240, bubbles: true, cancelable: true" +
                        "  }));" +
                        "}"
        );
        semantics.pauseAfterScroll();
    }

    private void openCreateAdForm() {
        semantics.enableFlutterSemantics();
        if (isCreateAdFormLoaded(driver)) {
            return;
        }
        if (!waitUntil(this::isAdvertisementsPageLoaded, PAGE_POLL)) {
            throw new NoSuchElementException(
                    "Advertisements page is not loaded (expected '" + MY_ADS_TEXT + "' or '" + CREATE_ADS_TEXT + "')");
        }
        scrollToCreateAdsAction();
        for (int attempt = 0; attempt < MAX_ACTION_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                scrollToCreateAdsAction();
            }
            if (clickSemanticsAction(SEMANTICS_CREATE_AD, CREATE_ADS_TEXT)
                    && waitUntil(this::isCreateAdFormLoaded, DEFAULT_WAIT)) {
                return;
            }
            semantics.pauseAfterScroll();
        }
        if (isCreateAdFormLoaded(driver)) {
            return;
        }
        throw new NoSuchElementException(
                "Create Ads button not found (expected semantics " + SEMANTICS_CREATE_AD + " or text '" + CREATE_ADS_TEXT + "')");
    }

    private void enterField(String semanticsLabel, String fallbackLabel, String value) {
        semantics.enableFlutterSemantics();
        if (!waitUntil(this::isCreateAdFormLoaded, DEFAULT_WAIT)) {
            throw new NoSuchElementException("Create ad form is not visible for field: " + semanticsLabel);
        }
        scrollToFieldLabel(semanticsLabel, fallbackLabel);
        WebElement input = findFormInput(semanticsLabel, fallbackLabel);
        typeExactValue(input, value);
    }

    private WebElement findFormInput(String semanticsLabel, String fallbackLabel) {
        for (String label : new String[]{semanticsLabel, fallbackLabel}) {
            By wrappedInput = semanticsFieldInput(label);
            List<WebElement> wrapped = driver.findElements(wrappedInput);
            if (!wrapped.isEmpty()) {
                return wait.until(ExpectedConditions.elementToBeClickable(wrapped.get(0)));
            }
            List<WebElement> direct = driver.findElements(inputField(label));
            if (!direct.isEmpty()) {
                return wait.until(ExpectedConditions.elementToBeClickable(direct.get(0)));
            }
        }
        throw new NoSuchElementException(
                "Form input not found for field: " + semanticsLabel + " / " + fallbackLabel);
    }

    private WebElement findAdDescriptionInput() {
        Object element = ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const wanted = arguments[0];" +
                        "const semanticsHints = ['ad_description_field', 'ad_description', wanted];" +
                        "function isDescriptionTextarea(el) {" +
                        "  if (!el || el.tagName !== 'TEXTAREA' || isInNav(el)) return false;" +
                        "  const label = (el.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return !label.includes('campaign') && !label.includes('title')" +
                        "    && !label.includes('url') && !label.includes('website');" +
                        "}" +
                        "function textareaIn(node) {" +
                        "  if (!node) return null;" +
                        "  const direct = node.querySelector('textarea');" +
                        "  return isDescriptionTextarea(direct) ? direct : null;" +
                        "}" +
                        "for (const hint of semanticsHints) {" +
                        "  const node = findOutsideNav('flt-semantics[aria-label=\"' + hint + '\"]')" +
                        "    || findOutsideNav('flt-semantics[aria-label*=\"' + hint + '\"]');" +
                        "  const match = textareaIn(node);" +
                        "  if (match) return match;" +
                        "}" +
                        "const byAria = document.querySelector('textarea[aria-label=\"' + wanted + '\"]');" +
                        "if (isDescriptionTextarea(byAria)) return byAria;" +
                        "const labelNode = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = node.getAttribute('aria-label') || '';" +
                        "  return text === wanted || label === wanted;" +
                        "});" +
                        "const inLabel = textareaIn(labelNode);" +
                        "if (inLabel) return inLabel;" +
                        "if (labelNode) {" +
                        "  const labelRect = labelNode.getBoundingClientRect();" +
                        "  let nearest = null;" +
                        "  let nearestDistance = Infinity;" +
                        "  Array.from(document.querySelectorAll('textarea')).forEach(candidate => {" +
                        "    if (!isDescriptionTextarea(candidate)) return;" +
                        "    const rect = candidate.getBoundingClientRect();" +
                        "    const verticalGap = rect.top - labelRect.bottom;" +
                        "    if (verticalGap < -8) return;" +
                        "    const distance = Math.abs(verticalGap) + Math.abs(rect.left - labelRect.left);" +
                        "    if (distance < nearestDistance) {" +
                        "      nearestDistance = distance;" +
                        "      nearest = candidate;" +
                        "    }" +
                        "  });" +
                        "  if (nearest) return nearest;" +
                        "}" +
                        "const textareas = Array.from(document.querySelectorAll('textarea'))" +
                        "  .filter(el => isDescriptionTextarea(el));" +
                        "return textareas.length > 0 ? textareas[textareas.length - 1] : null;",
                FALLBACK_AD_DESCRIPTION
        );
        if (element instanceof WebElement webElement) {
            return wait.until(ExpectedConditions.elementToBeClickable(webElement));
        }
        throw new NoSuchElementException(
                "Ad Description textarea not found (expected label '" + FALLBACK_AD_DESCRIPTION + "')");
    }

    private void blurActiveElement() {
        ((JavascriptExecutor) driver).executeScript(
                "if (document.activeElement && document.activeElement !== document.body) {" +
                        "  document.activeElement.blur();" +
                        "}"
        );
    }

    private boolean clickUploadMediaViaScript() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const extra = Array.from(document.querySelectorAll('flt-semantics')).filter(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = node.getAttribute('aria-label') || '';" +
                        "  return text === 'Upload here' || text.includes('Upload Image') || label.includes('upload_media');" +
                        "});" +
                        "const candidates = [" +
                        "  findOutsideNav('flt-semantics[aria-label=\"upload_media_button\"]')," +
                        "  findOutsideNav('flt-semantics[aria-label*=\"upload_media\"]')" +
                        "].concat(extra);" +
                        "for (const node of candidates) {" +
                        "  if (!node) continue;" +
                        "  const target = node.closest('flt-semantics[flt-tappable]') || node;" +
                        "  target.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "  if (dispatchPointerClick(target)) return true;" +
                        "}" +
                        "return false;"
        ));
    }

    private boolean uploadMediaWithFileChooser(String filePath) {
        String absolutePath = Paths.get(filePath).toAbsolutePath().normalize().toString();
        if (!Files.exists(Paths.get(absolutePath))) {
            return false;
        }

        // Headless-first path: intercept chooser + attach via CDP/DOM (needs Chrome CDP match).
        if (uploadViaInterceptedFileChooser(absolutePath)) {
            recoverUiAfterUpload();
            return true;
        }

        if (!clickUploadMediaControl()) {
            throw new NoSuchElementException(
                    "Upload media button not found (expected semantics " + SEMANTICS_UPLOAD_MEDIA
                            + " or text '" + FALLBACK_UPLOAD_HERE + "')");
        }

        if (uploadByRacingFileInput(absolutePath, Duration.ofSeconds(12))) {
            recoverUiAfterUpload();
            return true;
        }

        // Headless cannot use OS dialogs; only allow dialog fallbacks when headed.
        if (!isHeadlessEnvironment()) {
            if (isLinuxHost() && uploadViaXdotool(absolutePath, true) && confirmDialogUploadSucceeded()) {
                return true;
            }
            uploadMediaViaNativeDialog(absolutePath);
            recoverUiAfterUpload();
            if (waitUntil(this::isPublishActionPresent, DEFAULT_WAIT)
                    || waitUntil(this::isMediaLikelyAttached, Duration.ofSeconds(5))) {
                return true;
            }
            return !isContinuousIntegration();
        }
        return false;
    }

    private boolean uploadByRacingFileInput(String absolutePath) {
        return uploadByRacingFileInput(absolutePath, Duration.ofSeconds(8));
    }

    private boolean uploadByRacingFileInput(String absolutePath, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (sendKeysToUploadFileInput(absolutePath) || setFileInputViaCdp(absolutePath)) {
                semantics.pauseAfterScroll();
                return true;
            }
            revealFileInputsViaScript();
            if (injectAndUseHiddenFileInput(absolutePath)) {
                semantics.pauseAfterScroll();
                return true;
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * Last-resort headless helper: create a real file input, set the path, then re-click
     * upload_media_button so Flutter's picker can consume an already-available input when present.
     */
    private boolean injectAndUseHiddenFileInput(String absolutePath) {
        try {
            Object created = ((JavascriptExecutor) driver).executeScript(
                    "let input = document.querySelector('input[data-vi-selenium-upload=\"true\"]');" +
                            "if (!input) {" +
                            "  input = document.createElement('input');" +
                            "  input.type = 'file';" +
                            "  input.accept = 'image/*';" +
                            "  input.setAttribute('data-vi-selenium-upload', 'true');" +
                            "  input.style.position = 'fixed';" +
                            "  input.style.left = '0';" +
                            "  input.style.top = '0';" +
                            "  input.style.opacity = '0.01';" +
                            "  input.style.zIndex = '999999';" +
                            "  document.body.appendChild(input);" +
                            "}" +
                            "return input;"
            );
            if (!(created instanceof WebElement fileInput)) {
                return false;
            }
            return sendKeysToFileInput(fileInput, absolutePath);
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean uploadViaXdotool(String absolutePath, boolean chooserLikelyOpen) {
        if (!isCommandAvailable("xdotool")) {
            return false;
        }
        try {
            if (!chooserLikelyOpen) {
                clickUploadMediaControl();
            }
            Thread.sleep(chooserLikelyOpen ? 800 : 1200);
            String script = "xdotool key --clearmodifiers ctrl+l && "
                    + "sleep 0.2 && "
                    + "xdotool type --delay 8 --clearmodifiers " + shellQuote(absolutePath) + " && "
                    + "xdotool key --clearmodifiers Return && "
                    + "sleep 0.5 && "
                    + "xdotool key --clearmodifiers Return";
            Process process = new ProcessBuilder("bash", "-lc", script)
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(20, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return false;
            }
            semantics.pauseAfterScroll();
            Thread.sleep(1000);
            return process.exitValue() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String shellQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }

    private boolean isCommandAvailable(String command) {
        try {
            Process process = new ProcessBuilder("bash", "-lc", "command -v " + command)
                    .redirectErrorStream(true)
                    .start();
            return process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isLinuxHost() {
        return System.getProperty("os.name", "").toLowerCase().contains("linux");
    }

    private boolean isLinuxCi() {
        return isContinuousIntegration() && isLinuxHost();
    }

    private void recoverUiAfterUpload() {
        // Close any leftover chooser without thrashing Flutter a11y.
        dismissBlockingUi();
        semantics.enableFlutterSemantics();
        semantics.forceEnableFlutterSemantics();
        waitUntil(this::isPublishActionPresent, Duration.ofSeconds(15));
    }

    private boolean confirmDialogUploadSucceeded() {
        recoverUiAfterUpload();
        if (waitUntil(this::isPublishActionPresent, Duration.ofSeconds(10))) {
            return true;
        }
        return waitUntil(this::isMediaLikelyAttached, Duration.ofSeconds(5));
    }

    private boolean isMediaLikelyAttached(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        if (text != null) {
            String normalized = text.toLowerCase();
            if (normalized.contains("carrer")
                    || normalized.contains("career")
                    || normalized.contains("replace")
                    || normalized.contains("change image")
                    || normalized.contains("remove")) {
                return true;
            }
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return Array.from(document.querySelectorAll('img, flt-semantics')).some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  if (node.tagName === 'IMG') {" +
                        "    const src = (node.getAttribute('src') || '').toLowerCase();" +
                        "    return src.includes('blob:') || src.includes('carrer') || src.includes('data:image');" +
                        "  }" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim().toLowerCase();" +
                        "  return label.includes('carrer') || text.includes('carrer') || text.includes('replace');" +
                        "});"
        ));
    }

    private boolean uploadViaInterceptedFileChooser(String absolutePath) {
        if (!(driver instanceof ChromeDriver chromeDriver)) {
            return false;
        }
        DevTools devTools = null;
        CountDownLatch chooserOpened = new CountDownLatch(1);
        AtomicReference<Integer> backendNodeId = new AtomicReference<>();
        boolean interceptEnabled = false;
        try {
            try {
                chromeDriver.executeCdpCommand(
                        "Page.setInterceptFileChooserDialog",
                        Map.of("enabled", true)
                );
                interceptEnabled = true;
            } catch (Exception ignored) {
                // Fall through to typed DevTools command.
            }

            try {
                devTools = chromeDriver.getDevTools();
                devTools.createSessionIfThereIsNotOne();
                Event<Integer> fileChooserOpened = new Event<>(
                        "Page.fileChooserOpened",
                        input -> {
                            Integer id = null;
                            input.beginObject();
                            while (input.hasNext()) {
                                String name = input.nextName();
                                if ("backendNodeId".equals(name)) {
                                    id = input.read(Integer.class);
                                } else {
                                    input.skipValue();
                                }
                            }
                            input.endObject();
                            return id;
                        }
                );
                devTools.addListener(fileChooserOpened, id -> {
                    if (id != null && id > 0) {
                        backendNodeId.set(id);
                        chooserOpened.countDown();
                    }
                });
                if (!interceptEnabled) {
                    devTools.send(new Command<>(
                            "Page.setInterceptFileChooserDialog",
                            Map.of("enabled", true)
                    ));
                    interceptEnabled = true;
                }
            } catch (Exception ignored) {
                // Typed DevTools may fail when CDP versions mismatch; executeCdpCommand can still work.
            }

            if (!interceptEnabled) {
                return false;
            }
            if (!clickUploadMediaControl()) {
                return false;
            }

            // Prefer event backendNodeId when available.
            if (chooserOpened.await(5, TimeUnit.SECONDS) && backendNodeId.get() != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("files", List.of(absolutePath));
                params.put("backendNodeId", backendNodeId.get());
                try {
                    chromeDriver.executeCdpCommand("DOM.setFileInputFiles", params);
                    semantics.pauseAfterScroll();
                    return true;
                } catch (Exception ignored) {
                    if (devTools != null) {
                        try {
                            devTools.send(new Command<>("DOM.setFileInputFiles", params));
                            semantics.pauseAfterScroll();
                            return true;
                        } catch (Exception ignoredAgain) {
                            // Fall through to DOM race.
                        }
                    }
                }
            }

            // Intercept keeps the OS dialog closed; poll Flutter's temporary file input.
            return uploadByRacingFileInput(absolutePath, Duration.ofSeconds(15));
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception ignored) {
            return false;
        } finally {
            if (devTools != null) {
                try {
                    devTools.clearListeners();
                } catch (Exception ignored) {
                    // Best-effort cleanup.
                }
            }
            if (interceptEnabled) {
                try {
                    chromeDriver.executeCdpCommand(
                            "Page.setInterceptFileChooserDialog",
                            Map.of("enabled", false)
                    );
                } catch (Exception ignored) {
                    if (devTools != null) {
                        try {
                            devTools.send(new Command<>(
                                    "Page.setInterceptFileChooserDialog",
                                    Map.of("enabled", false)
                            ));
                        } catch (Exception ignoredAgain) {
                            // Best-effort cleanup.
                        }
                    }
                }
            }
        }
    }

    private boolean uploadMediaFile(String filePath) {
        semantics.pauseAfterScroll();
        blurActiveElement();
        String absolutePath = Paths.get(filePath).toAbsolutePath().normalize().toString();
        if (sendKeysToUploadFileInput(absolutePath) || sendKeysToUploadFileInput(filePath)) {
            return true;
        }
        revealFileInputsViaScript();
        if (sendKeysToUploadFileInput(absolutePath) || sendKeysToUploadFileInput(filePath)) {
            return true;
        }
        if (waitUntil(this::hasFileInput, DEFAULT_WAIT)) {
            if (sendKeysToUploadFileInput(absolutePath) || sendKeysToUploadFileInput(filePath)) {
                return true;
            }
            if (setFileInputViaCdp(absolutePath)) {
                return true;
            }
        }
        if (setFileInputViaCdp(absolutePath)) {
            return true;
        }
        if (!isHeadlessEnvironment()) {
            clickUploadMediaControl();
            uploadMediaViaNativeDialog(absolutePath);
            dismissBlockingUi();
            semantics.forceEnableFlutterSemantics();
            return waitUntil(this::isPublishActionPresent, DEFAULT_WAIT)
                    || waitUntil(this::isMediaLikelyAttached, Duration.ofSeconds(5));
        }
        return false;
    }

    private boolean setFileInputViaCdp(String absolutePath) {
        if (!(driver instanceof ChromeDriver chromeDriver)) {
            return false;
        }
        try {
            Map<String, Object> document = chromeDriver.executeCdpCommand(
                    "DOM.getDocument",
                    Map.of("depth", -1, "pierce", true)
            );
            @SuppressWarnings("unchecked")
            Map<String, Object> root = (Map<String, Object>) document.get("root");
            if (root == null || root.get("nodeId") == null) {
                return false;
            }
            int rootNodeId = ((Number) root.get("nodeId")).intValue();
            Integer fileInputNodeId = findFileInputNodeId(chromeDriver, root, rootNodeId);
            if (fileInputNodeId == null || fileInputNodeId <= 0) {
                return false;
            }
            chromeDriver.executeCdpCommand(
                    "DOM.setFileInputFiles",
                    Map.of("nodeId", fileInputNodeId, "files", List.of(absolutePath))
            );
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Integer findFileInputNodeId(
            ChromeDriver chromeDriver,
            Map<String, Object> node,
            int fallbackRootNodeId
    ) {
        try {
            Map<String, Object> queryResult = chromeDriver.executeCdpCommand(
                    "DOM.querySelector",
                    Map.of("nodeId", fallbackRootNodeId, "selector", "input[type='file']")
            );
            if (queryResult != null && queryResult.get("nodeId") != null) {
                int nodeId = ((Number) queryResult.get("nodeId")).intValue();
                if (nodeId > 0) {
                    return nodeId;
                }
            }
        } catch (Exception ignored) {
            // Fall through to recursive search.
        }
        return findFileInputNodeIdRecursive(node);
    }

    @SuppressWarnings("unchecked")
    private Integer findFileInputNodeIdRecursive(Map<String, Object> node) {
        if (node == null) {
            return null;
        }
        Object nodeName = node.get("nodeName");
        Object attributes = node.get("attributes");
        if ("INPUT".equals(nodeName) && attributes instanceof List<?> attributeList) {
            for (int i = 0; i + 1 < attributeList.size(); i += 2) {
                if ("type".equals(String.valueOf(attributeList.get(i)))
                        && "file".equalsIgnoreCase(String.valueOf(attributeList.get(i + 1)))) {
                    Object nodeId = node.get("nodeId");
                    return nodeId instanceof Number number ? number.intValue() : null;
                }
            }
        }
        Object children = node.get("children");
        if (children instanceof List<?> childList) {
            for (Object child : childList) {
                if (child instanceof Map<?, ?> childMap) {
                    Integer nested = findFileInputNodeIdRecursive((Map<String, Object>) childMap);
                    if (nested != null) {
                        return nested;
                    }
                }
            }
        }
        Object shadowRoots = node.get("shadowRoots");
        if (shadowRoots instanceof List<?> shadowList) {
            for (Object shadow : shadowList) {
                if (shadow instanceof Map<?, ?> shadowMap) {
                    Integer nested = findFileInputNodeIdRecursive((Map<String, Object>) shadowMap);
                    if (nested != null) {
                        return nested;
                    }
                }
            }
        }
        Object contentDocument = node.get("contentDocument");
        if (contentDocument instanceof Map<?, ?> contentMap) {
            return findFileInputNodeIdRecursive((Map<String, Object>) contentMap);
        }
        return null;
    }

    private boolean isHeadlessEnvironment() {
        if ("false".equalsIgnoreCase(System.getenv("HEADLESS"))) {
            return false;
        }
        return isContinuousIntegration()
                || "true".equalsIgnoreCase(System.getenv("HEADLESS"));
    }

    private boolean isContinuousIntegration() {
        return "true".equalsIgnoreCase(System.getenv("CI"));
    }

    private void scrollFormDown() {
        ((JavascriptExecutor) driver).executeScript(
                "function isScrollable(el) {" +
                        "  if (!el) return false;" +
                        "  const style = window.getComputedStyle(el);" +
                        "  const oy = style.overflowY;" +
                        "  return (oy === 'auto' || oy === 'scroll') && el.scrollHeight > el.clientHeight + 8;" +
                        "}" +
                        "const delta = Math.max(window.innerHeight * 0.75, 320);" +
                        "window.scrollBy(0, delta);" +
                        "const host = document.querySelector('flt-semantics-host');" +
                        "if (host) host.scrollTop += delta;" +
                        "Array.from(document.querySelectorAll('*')).forEach(el => {" +
                        "  if (!isScrollable(el)) return;" +
                        "  const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "  if (menu && menu.contains(el)) return;" +
                        "  el.scrollTop += delta;" +
                        "});"
        );
        semantics.pauseAfterScroll();
    }

    private String resolveAdMediaPath(String mediaPath) {
        if (mediaPath != null && !mediaPath.isBlank()) {
            Path provided = Paths.get(mediaPath);
            if (!provided.isAbsolute()) {
                provided = Paths.get(System.getProperty("user.dir"), mediaPath);
            }
            provided = provided.toAbsolutePath().normalize();
            if (Files.exists(provided)) {
                return provided.toString();
            }
            Path byFileName = Paths.get("target/test-classes/testdata", provided.getFileName().toString())
                    .toAbsolutePath()
                    .normalize();
            if (Files.exists(byFileName)) {
                return byFileName.toString();
            }
            throw new NoSuchElementException("Ad media file not found at provided path: " + provided);
        }

        Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads", AD_MEDIA_FILE_NAME)
                .toAbsolutePath()
                .normalize();
        if (Files.exists(downloadsPath)) {
            return downloadsPath.toString();
        }
        Path testDataPath = Paths.get("src/test/resources/testdata", AD_MEDIA_FILE_NAME)
                .toAbsolutePath()
                .normalize();
        if (Files.exists(testDataPath)) {
            return testDataPath.toString();
        }
        Path classpathRelative = Paths.get("target/test-classes/testdata", AD_MEDIA_FILE_NAME)
                .toAbsolutePath()
                .normalize();
        if (Files.exists(classpathRelative)) {
            return classpathRelative.toString();
        }
        throw new NoSuchElementException(
                "Ad media file not found. Expected at " + downloadsPath + " or " + testDataPath);
    }

    private boolean hasFileInput(WebDriver webDriver) {
        return !webDriver.findElements(By.cssSelector("input[type='file']")).isEmpty()
                || Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "const walk = (node) => {" +
                        "  if (!node) return false;" +
                        "  if (node.nodeName === 'INPUT' && node.type === 'file') return true;" +
                        "  if (node.shadowRoot && walk(node.shadowRoot)) return true;" +
                        "  const children = node.children || [];" +
                        "  for (let i = 0; i < children.length; i++) {" +
                        "    if (walk(children[i])) return true;" +
                        "  }" +
                        "  return false;" +
                        "};" +
                        "return walk(document.body);"
        ));
    }

    private boolean sendKeysToUploadFileInput(String filePath) {
        List<WebElement> fileInputs = driver.findElements(By.cssSelector(
                "flt-semantics[aria-label*='upload_media'] input[type='file'],"
                        + " flt-semantics[aria-label*='upload_media_button'] input[type='file'],"
                        + " input[type='file']"
        ));
        if (fileInputs.isEmpty()) {
            WebElement deepInput = findFileInputInShadowDom();
            if (deepInput != null) {
                fileInputs = List.of(deepInput);
            }
        }
        if (fileInputs.isEmpty()) {
            return false;
        }
        for (int index = fileInputs.size() - 1; index >= 0; index--) {
            if (sendKeysToFileInput(fileInputs.get(index), filePath)) {
                return true;
            }
        }
        return false;
    }

    private WebElement findFileInputInShadowDom() {
        Object node = ((JavascriptExecutor) driver).executeScript(
                "const walk = (root, found) => {" +
                        "  if (!root) return found;" +
                        "  const nodes = root.querySelectorAll ? root.querySelectorAll('input[type=\"file\"]') : [];" +
                        "  nodes.forEach(node => found.push(node));" +
                        "  const all = root.querySelectorAll ? root.querySelectorAll('*') : [];" +
                        "  all.forEach(el => { if (el.shadowRoot) walk(el.shadowRoot, found); });" +
                        "  return found;" +
                        "};" +
                        "const inputs = walk(document, []);" +
                        "return inputs.length ? inputs[inputs.length - 1] : null;"
        );
        return node instanceof WebElement webElement ? webElement : null;
    }

    private boolean sendKeysToFileInputIfPresent(String filePath) {
        return sendKeysToUploadFileInput(filePath);
    }

    private boolean sendKeysToFileInput(WebElement fileInput, String filePath) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.display = 'block';" +
                            "arguments[0].style.visibility = 'visible';" +
                            "arguments[0].style.opacity = '1';" +
                            "arguments[0].removeAttribute('hidden');",
                    fileInput
            );
            fileInput.sendKeys(filePath);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void revealFileInputsViaScript() {
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('input[type=\"file\"]').forEach(input => {" +
                        "  input.style.display = 'block';" +
                        "  input.style.visibility = 'visible';" +
                        "  input.style.opacity = '1';" +
                        "  input.removeAttribute('hidden');" +
                        "});"
        );
    }

    private void uploadMediaViaNativeDialog(String filePath) {
        try {
            boolean isMac = System.getProperty("os.name", "").toLowerCase().contains("mac");
            blurActiveElement();
            Thread.sleep(300);
            StringSelection selection = new StringSelection(filePath);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            Robot robot = new Robot();
            robot.setAutoDelay(150);
            Thread.sleep(1500);
            if (isMac) {
                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_G);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_META);
            } else {
                // Chrome/GTK file chooser under Xvfb: focus location field, then paste path.
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_L);
                robot.keyRelease(KeyEvent.VK_L);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                Thread.sleep(300);
                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_N);
                robot.keyRelease(KeyEvent.VK_N);
                robot.keyRelease(KeyEvent.VK_ALT);
            }
            Thread.sleep(400);
            robot.keyPress(isMac ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(isMac ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
            Thread.sleep(500);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(800);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(1500);
        } catch (Exception exception) {
            throw new NoSuchElementException(
                    "Failed to upload media via native file dialog: " + exception.getMessage());
        }
    }

    private boolean clickSemanticsAction(String semanticsLabel, String visibleText) {
        semantics.enableFlutterSemantics();
        if (semantics.clickSemanticsLabelViaScript(semanticsLabel)) {
            return true;
        }
        if (clickActionViaScript(semanticsLabel, visibleText)) {
            return true;
        }
        if (semantics.clickVisibleText(visibleText)) {
            return true;
        }
        return false;
    }

    private boolean clickActionViaScript(String semanticsLabel, String visibleText) {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        DISPATCH_POINTER_CLICK_SCRIPT +
                        "const semanticsLabel = arguments[0];" +
                        "const wanted = arguments[1];" +
                        "let node = findOutsideNav('flt-semantics[aria-label=\"' + semanticsLabel + '\"]')" +
                        "  || findOutsideNav('flt-semantics[aria-label*=\"' + semanticsLabel + '\"]');" +
                        "if (!node) {" +
                        "  node = Array.from(document.querySelectorAll('flt-semantics')).find(candidate => {" +
                        "    if (isInNav(candidate)) return false;" +
                        "    return (candidate.textContent || '').replace(/\\s+/g, ' ').trim() === wanted;" +
                        "  }) || null;" +
                        "}" +
                        "if (!node) return false;" +
                        "node.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "return dispatchPointerClick(node.closest('flt-semantics[flt-tappable]') || node);",
                semanticsLabel,
                visibleText
        ));
    }

    private void scrollNavigationToAdvertisements() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "const menu = document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                        "if (!menu) return;" +
                        "const target = Array.from(menu.querySelectorAll('flt-semantics')).find(node => {" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  return label.includes('advertisement') || text === 'Advertisements';" +
                        "});" +
                        "if (target) target.scrollIntoView({block: 'center', inline: 'nearest'});"
        );
        semantics.pauseAfterScroll();
    }

    private void scrollToCreateAdsAction() {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, 0);" +
                        FIND_OUTSIDE_NAV_SCRIPT +
                        "const anchor = findOutsideNav('flt-semantics[aria-label=\"create_ad_button\"]')" +
                        "  || Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                        "    if (isInNav(node)) return false;" +
                        "    return (node.textContent || '').replace(/\\s+/g, ' ').trim() === 'Create Ads';" +
                        "  });" +
                        "if (anchor) anchor.scrollIntoView({block: 'center', inline: 'nearest'});"
        );
        semantics.pauseAfterScroll();
    }

    private void scrollToFieldLabel(String semanticsLabel, String fallbackLabel) {
        semantics.enableFlutterSemantics();
        ((JavascriptExecutor) driver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "const labels = [arguments[0], arguments[1]];" +
                        "for (const label of labels) {" +
                        "  const anchor = findOutsideNav('flt-semantics[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('input[aria-label=\"' + label + '\"]')" +
                        "    || document.querySelector('textarea[aria-label=\"' + label + '\"]');" +
                        "  if (anchor && !isInNav(anchor)) {" +
                        "    anchor.scrollIntoView({block: 'center', inline: 'nearest'});" +
                        "    break;" +
                        "  }" +
                        "}",
                semanticsLabel,
                fallbackLabel
        );
        semantics.pauseAfterScroll();
    }

    private void typeExactValue(WebElement input, String value) {
        semantics.enableFlutterSemantics();
        semantics.scrollIntoView(input);
        if (!semantics.clickElementReliably(input)) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", input);
        }
        try {
            input.sendKeys(Keys.chord(SELECT_ALL_MODIFIER, "a"));
            input.sendKeys(Keys.BACK_SPACE);
            input.sendKeys(value);
            String currentValue = input.getAttribute("value");
            if (currentValue == null || !currentValue.equals(value)) {
                setInputValueViaScript(input, value);
            }
        } catch (Exception ex) {
            setInputValueViaScript(input, value);
        }
    }

    private void setInputValueViaScript(WebElement input, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];" +
                        "const val = arguments[1];" +
                        "el.focus();" +
                        "el.value = val;" +
                        "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "el.dispatchEvent(new Event('change', { bubbles: true }));",
                input,
                value
        );
    }

    private void clickSemanticsElement(By locator) {
        semantics.clickSemanticsElement(locator, wait);
    }

    private void pauseAfterNavigation() {
        semantics.pauseAfterScroll();
    }

    private boolean waitUntil(Function<WebDriver, Boolean> condition, Duration timeout) {
        return semantics.waitUntil(condition, timeout);
    }

    private boolean isHomePageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        String text = semantics.getSemanticsText(webDriver);
        return text != null
                && (text.contains("Individual Users")
                || text.contains("Company Users")
                || text.contains(ADVERTISEMENTS_TEXT)
                || text.contains("Navigation menu"));
    }

    private boolean isAdvertisementsPageLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        if (isCreateAdFormLoaded(webDriver)) {
            return true;
        }
        String text = semantics.getSemanticsText(webDriver);
        return text != null && text.contains(MY_ADS_TEXT) && text.contains(CREATE_ADS_TEXT);
    }

    private boolean isCreateAdFormLoaded(WebDriver webDriver) {
        semantics.enableFlutterSemanticsOn(webDriver);
        return isInputFieldPresent(webDriver, SEMANTICS_CAMPAIGN_NAME, FALLBACK_CAMPAIGN_NAME)
                || isInputFieldPresent(webDriver, SEMANTICS_WEBSITE_URL, FALLBACK_WEBSITE_URL)
                || isInputFieldPresent(webDriver, SEMANTICS_AD_TITLE, FALLBACK_AD_TITLE)
                || isInputFieldPresent(webDriver, SEMANTICS_AD_DESCRIPTION, FALLBACK_AD_DESCRIPTION)
                || isUploadMediaPresent(webDriver)
                || isPublishActionPresent(webDriver);
    }

    private boolean isUploadSectionReady(WebDriver webDriver) {
        return isCreateAdFormLoaded(webDriver) || hasFileInput(webDriver);
    }

    private boolean isUploadMediaPresent(WebDriver webDriver) {
        if (!webDriver.findElements(By.cssSelector(
                "flt-semantics[aria-label='" + SEMANTICS_UPLOAD_MEDIA + "'],"
                        + " flt-semantics[aria-label*='" + SEMANTICS_UPLOAD_MEDIA + "']")).isEmpty()) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = node.getAttribute('aria-label') || '';" +
                        "  return text === 'Upload here'" +
                        "    || text.includes('Upload Image')" +
                        "    || label === 'upload_media_button'" +
                        "    || label.includes('upload_media');" +
                        "});"
        ));
    }

    private boolean isPublishActionPresent(WebDriver webDriver) {
        if (isPublishButtonSemanticsPresent(webDriver)) {
            return true;
        }
        return Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                FIND_OUTSIDE_NAV_SCRIPT +
                        "return Array.from(document.querySelectorAll('flt-semantics')).some(node => {" +
                        "  if (isInNav(node)) return false;" +
                        "  const text = (node.textContent || '').replace(/\\s+/g, ' ').trim();" +
                        "  const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                        "  return text === '" + PUBLISH_TEXT + "' || label === 'publish_button' || label.includes('publish');" +
                        "});"
        ));
    }

    private boolean isInputFieldPresent(WebDriver webDriver, String semanticsLabel, String fallbackLabel) {
        for (String label : new String[]{semanticsLabel, fallbackLabel}) {
            if (!webDriver.findElements(inputField(label)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isFieldPresent(WebDriver webDriver, String semanticsLabel, String fallbackLabel) {
        return isInputFieldPresent(webDriver, semanticsLabel, fallbackLabel);
    }

    private static final String FIND_OUTSIDE_NAV_SCRIPT =
            "function navMenu() {" +
                    "  return document.querySelector('flt-semantics[aria-label=\"Navigation menu\"]');" +
                    "}" +
                    "function isInNav(node) {" +
                    "  const menu = navMenu();" +
                    "  return !!(menu && node && menu.contains(node));" +
                    "}" +
                    "function findOutsideNav(selector) {" +
                    "  const menu = navMenu();" +
                    "  return Array.from(document.querySelectorAll(selector)).find(node => !menu || !menu.contains(node)) || null;" +
                    "}";

    private static final String DISPATCH_POINTER_CLICK_SCRIPT =
            "function dispatchPointerClick(target) {" +
                    "  const rect = target.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  const x = rect.left + rect.width / 2;" +
                    "  const y = rect.top + rect.height / 2;" +
                    "  const hit = document.elementFromPoint(x, y) || target;" +
                    "  ['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => {" +
                    "    hit.dispatchEvent(new MouseEvent(type, { bubbles: true, cancelable: true, clientX: x, clientY: y }));" +
                    "  });" +
                    "  return true;" +
                    "}";

    private static By semanticsButton(String label) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + label + "']" +
                        " | //flt-semantics[contains(@aria-label,'" + label + "')]");
    }

    private static By inputField(String label) {
        return By.cssSelector("input[aria-label='" + label + "'], textarea[aria-label='" + label + "']");
    }

    private static By semanticsFieldInput(String label) {
        return By.xpath(
                "//flt-semantics[@aria-label='" + label + "']//input" +
                        " | //flt-semantics[contains(@aria-label,'" + label + "')]//input" +
                        " | //flt-semantics[@aria-label='" + label + "']//textarea" +
                        " | //flt-semantics[contains(@aria-label,'" + label + "')]//textarea" +
                        " | //input[@aria-label='" + label + "']" +
                        " | //textarea[@aria-label='" + label + "']" +
                        " | //input[contains(@aria-label,'" + label + "')]" +
                        " | //textarea[contains(@aria-label,'" + label + "')]");
    }
}
