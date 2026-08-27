Feature: Input Categories Add, Edit and Delete


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page

  Scenario: Validate Input Categories menu
    When user clicks on Input Categories menu
    Then user validate Input Categories title

  @add_input_category
  Scenario: Add Input Categories industry name
    When user clicks on Input Categories menu
    And user clicks on What's important to you
    And user click on Add New
    And user enter English Name "Banking"
    And user enter Spanish Name "Banca"
    And user enter Portuguese Name "Bancário"
    And user enter Canadian Name "Banking"
    Then user click on Add Button

  @edit_input_category
  Scenario: Validate edit of Input Categories industry name
    When user clicks on Input Categories menu
    And user clicks on What's important to you
    And user click on Edit
    And user edit name "Blockchain "
    Then user click on Edit Save Button

  @delete_input_category
  Scenario: Validate delete of Input Categories industry name
    When user clicks on Input Categories menu
    And user clicks on What's important to you
    And user click on Delete
    Then user click on  confirm Delete Button
    And delete lookup detail API should return status 200

  @block_input_category
  Scenario: Validate block of Input Categories industry name
    When user clicks on Input Categories menu
    And user clicks on What's important to you
    And user click on Block
    Then user click on  confirm Block Button
    And block lookup detail API should return status 200

  @add_input_category_role
  Scenario: Add Input Categories role name
    When user clicks on Input Categories menu
    And user clicks on More about you
    Then user validate More about you text
    And user click on Add New
    And user enter English Name "Accountant"
    And user enter Spanish Name "Contador"
    And user enter Portuguese Name "Contable"
    And user enter Canadian Name "Accountant"
    Then user click on Add Button

  @edit_input_category_role
  Scenario: Validate edit of Input Categories role name
    When user clicks on Input Categories menu
    And user clicks on More about you
    And user click on Edit
    And user edit name "KT Session "
    Then user click on Edit Save Button

  @delete_input_category_role
  Scenario: Validate delete of Input Categories role name
    When user clicks on Input Categories menu
    And user clicks on More about you
    And user click on Delete
    Then user click on  confirm Delete Button
    And delete lookup detail API should return status 200

  @block_input_category_role
  Scenario: Validate block of Input Categories role name
    When user clicks on Input Categories menu
    And user clicks on More about you
    And user click on Block
    Then user click on  confirm Block Button
    And block lookup detail API should return status 200


  @add_input_category_interest
  Scenario: Add Input Categories interest name
    When user clicks on Input Categories menu
    And user clicks on Interests and hobbies
    And user click on Add New
    And user enter English Name "Football"
    And user enter Spanish Name "Fútbol"
    And user enter Portuguese Name "Futebol"
    And user enter Canadian Name "Football"
    Then user click on Add Button


  @edit_input_category_interest
  Scenario: Validate edit of Input Categories interest name
    When user clicks on Input Categories menu
    And user clicks on Interests and hobbies
    And user click on Edit
    And user edit name "Swimming"
    Then user click on Edit Save Button

  @delete_input_category_interest
  Scenario: Validate delete of Input Categories interest name
    When user clicks on Input Categories menu
    And user clicks on Interests and hobbies
    And user click on Delete
    Then user click on  confirm Delete Button
    And delete lookup detail API should return status 200

  @block_input_category_interest
  Scenario: Validate block of Input Categories interest name
    When user clicks on Input Categories menu
    And user clicks on Interests and hobbies
    And user click on Block
    Then user click on  confirm Block Button
    And block lookup detail API should return status 200

  @add_input_category_skill
  Scenario: Add Input Categories skill name
    When user clicks on Input Categories menu
    And user clicks on Professional background
    And user click on Add New
    And user enter English Name "Analyst"
    And user enter Spanish Name "Analista"
    And user enter Portuguese Name "Analista"
    And user enter Canadian Name "Analyst"
    Then user click on Add Button

  @edit_input_category_skill
  Scenario: Validate edit of Input Categories skill name
    When user clicks on Input Categories menu
    And user clicks on Professional background
    And user click on Edit
    And user edit name "Blogging"
    Then user click on Edit Save Button

  @delete_input_category_skill
  Scenario: Validate delete of Input Categories skill name
    # Delete: lookup_detail_delete_button_0 | Confirm: confirm_delete_button
    When user clicks on Input Categories menu
    And user clicks on Professional background
    And user click on Delete
    Then user click on  confirm Delete Button
    And delete lookup detail API should return status 200

  @block_input_category_skill
  Scenario: Validate block of Input Categories skill name
    When user clicks on Input Categories menu
    And user clicks on Professional background
    And user click on Block
    Then user click on  confirm Block Button
    And block lookup detail API should return status 200

















    



















