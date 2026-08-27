package com.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LookupDetailApiNetworkMonitor {

    public enum Action {
        DELETE("DELETE"),
        BLOCK("PUT", "PATCH");

        private final Set<String> httpMethods;

        Action(String... httpMethods) {
            this.httpMethods = Set.of(httpMethods);
        }

        boolean matchesRequest(String rawMessage) {
            for (String method : httpMethods) {
                if (rawMessage.contains("\"method\":\"" + method + "\"")
                        || rawMessage.contains("\"method\": \"" + method + "\"")) {
                    return true;
                }
            }
            return false;
        }

        String label() {
            return name();
        }
    }

    private static final String LOOKUP_DETAIL_PATH = "look-up-detail";
    private static final String EVENT_PATH = "event";
    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("\"requestId\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern RESPONSE_URL_PATTERN = Pattern.compile(
            "\"url\"\\s*:\\s*\"([^\"]*(?:" + LOOKUP_DETAIL_PATH + "|" + EVENT_PATH + ")[^\"]*)\"");
    private static final Pattern RESPONSE_STATUS_PATTERN = Pattern.compile("\"status\"\\s*:\\s*(\\d+)");

    private Action currentAction;
    private final Set<String> trackedRequestIds = ConcurrentHashMap.newKeySet();
    private final List<CapturedResponse> capturedResponses = new ArrayList<>();

    public static void prepareForDeleteCapture(WebDriver driver) {
        prepareForCapture(driver, Action.DELETE);
    }

    public static void prepareForBlockCapture(WebDriver driver) {
        prepareForCapture(driver, Action.BLOCK);
    }

    public static void prepareForCapture(WebDriver driver, Action action) {
        ChromeDriver chromeDriver = requireChrome(driver);
        chromeDriver.manage().logs().get(LogType.PERFORMANCE);
        Holder.MONITOR.reset(action);
    }

    public static boolean waitForDeleteStatus(WebDriver driver, int expectedStatus, Duration timeout) {
        return waitForStatus(driver, Action.DELETE, expectedStatus, timeout);
    }

    public static boolean waitForBlockStatus(WebDriver driver, int expectedStatus, Duration timeout) {
        return waitForStatus(driver, Action.BLOCK, expectedStatus, timeout);
    }

    public static boolean waitForStatus(WebDriver driver, Action action, int expectedStatus, Duration timeout) {
        LookupDetailApiNetworkMonitor monitor = Holder.MONITOR;
        ChromeDriver chromeDriver = requireChrome(driver);
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            monitor.poll(chromeDriver);
            if (monitor.hasStatus(action, expectedStatus)) {
                return true;
            }
            sleepQuietly(250);
        }
        monitor.poll(chromeDriver);
        return monitor.hasStatus(action, expectedStatus);
    }

    public static String lastCapturedDeleteSummary(WebDriver driver) {
        return lastCapturedSummary(driver, Action.DELETE);
    }

    public static String lastCapturedBlockSummary(WebDriver driver) {
        return lastCapturedSummary(driver, Action.BLOCK);
    }

    public static String lastCapturedSummary(WebDriver driver, Action action) {
        LookupDetailApiNetworkMonitor monitor = Holder.MONITOR;
        monitor.poll(requireChrome(driver));
        synchronized (monitor.capturedResponses) {
            List<CapturedResponse> matches = monitor.capturedResponses.stream()
                    .filter(response -> response.action() == action)
                    .toList();
            if (matches.isEmpty()) {
                return "No " + action.label() + " look-up-detail/event API response captured";
            }
            CapturedResponse latest = matches.get(matches.size() - 1);
            return "method=" + latest.httpMethod() + ", url=" + latest.url() + ", status=" + latest.status();
        }
    }

    private static ChromeDriver requireChrome(WebDriver driver) {
        if (!(driver instanceof ChromeDriver chromeDriver)) {
            throw new IllegalStateException("API network validation requires ChromeDriver");
        }
        return chromeDriver;
    }

    private void reset(Action action) {
        currentAction = action;
        trackedRequestIds.clear();
        synchronized (capturedResponses) {
            capturedResponses.clear();
        }
    }

    private boolean hasStatus(Action action, int expectedStatus) {
        synchronized (capturedResponses) {
            return capturedResponses.stream()
                    .anyMatch(response -> response.action() == action && response.status() == expectedStatus);
        }
    }

    private void poll(ChromeDriver driver) {
        for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {
            processLogMessage(entry.getMessage());
        }
    }

    private void processLogMessage(String rawMessage) {
        if (rawMessage == null || currentAction == null || !matchesMonitoredApiPath(rawMessage)) {
            return;
        }
        if (rawMessage.contains("Network.requestWillBeSent") && currentAction.matchesRequest(rawMessage)) {
            String requestId = firstMatch(REQUEST_ID_PATTERN, rawMessage);
            if (requestId != null) {
                trackedRequestIds.add(requestId);
            }
            return;
        }
        if (!rawMessage.contains("Network.responseReceived")) {
            return;
        }
        String requestId = firstMatch(REQUEST_ID_PATTERN, rawMessage);
        String url = firstMatch(RESPONSE_URL_PATTERN, rawMessage);
        String statusText = firstMatch(RESPONSE_STATUS_PATTERN, rawMessage);
        if (url == null || statusText == null) {
            return;
        }
        if (!trackedRequestIds.isEmpty() && (requestId == null || !trackedRequestIds.contains(requestId))) {
            return;
        }
        int status = Integer.parseInt(statusText);
        synchronized (capturedResponses) {
            capturedResponses.add(new CapturedResponse(currentAction, currentAction.name(), url, status, requestId));
        }
    }

    private static boolean matchesMonitoredApiPath(String rawMessage) {
        return rawMessage.contains(LOOKUP_DETAIL_PATH) || rawMessage.contains("/" + EVENT_PATH);
    }

    private static String firstMatch(Pattern pattern, String value) {
        Matcher matcher = pattern.matcher(value);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private record CapturedResponse(Action action, String httpMethod, String url, int status, String requestId) {
    }

    private static final class Holder {
        private static final LookupDetailApiNetworkMonitor MONITOR = new LookupDetailApiNetworkMonitor();
    }
}
