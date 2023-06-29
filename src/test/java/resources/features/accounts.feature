Feature: Account endpoints testing
# *@Description: This are the features for all Account endpoints

  Scenario: getting all Accounts from database
    Given I log in with the role "Role_Employee"
    When I send a GET request to "accounts"
    Then the response status code should be 200
    And the response should be an array of objects