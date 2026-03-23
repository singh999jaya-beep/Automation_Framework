Feature: Media Library Functionality


  Background:
    Given user is on the login page
    When user enters email "helloteamdemo+techahead@gmail.com" and password "H3770T34M"
    And user clicks on the login button
    Then user is navigated to the home page
    And user goes to Media Library



Scenario: Creating a new folder in media library
  
  When user clicks on Add New and selects New Folder
  And user enters new folder name "Sample Folder"
  Then user clicks on Create Folder

Scenario: Creating a new document in media library
  When user clicks on Add New and selects New Document
  And user browses and uploads document from path "C:\\Users\\Jaya\\Downloads\\calendar.ics"
  And user enters tags "all" and description "Sample Document"
  Then user clicks on Save File

Scenario: Creating a new link in media library
  When user clicks on Add New and selects Add New Link
  And user enters link text "Sample link" and URL "https://google.com"
  Then user clicks on Save Link



