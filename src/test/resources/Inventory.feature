Feature: Test pages form

  Scenario: Validate Successful submission
    Given an item is registered with name car, description car for events, and total quantity
    When the user fills out the loan form with user Simon, item ID, quantity , start date, and end date
    Then the loan should be registered successfully

    When the user fills out the loan form again with user Simon, item ID , quantity  start date , and end date
    Then the loan should not be registered