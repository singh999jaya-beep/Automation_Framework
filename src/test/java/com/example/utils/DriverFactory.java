package com.example.utils;

  import org.openqa.selenium.WebDriver;
  import org.openqa.selenium.chrome.ChromeDriver;
  import org.openqa.selenium.firefox.FirefoxDriver;

  import io.github.bonigarcia.wdm.WebDriverManager;

  public class DriverFactory {

      public static WebDriver createDriver(String browser) {
          if (browser.equalsIgnoreCase("chrome")) {
              WebDriverManager.chromedriver().setup();
              return new ChromeDriver();
          } else if (browser.equalsIgnoreCase("firefox")) {
              WebDriverManager.firefoxdriver().setup();
              return new FirefoxDriver();
          }
          // Default to Chrome if no browser is specified
          WebDriverManager.chromedriver().setup();
          return new ChromeDriver();
      }
  }
