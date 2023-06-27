Feature: User Management

  Background:
    Given the user has logged in as a normal user

  Scenario: Get all users
    When the client requests to get all users
    Then the server should respond with status code 200
    And the response body should contain a list of users

  Scenario: Register a user (Normal User)
    Given the user provides valid user details
    When the client requests to register the user
    Then the server should respond with status code 200
    And the response body should contain a success message

  Scenario: Delete a user (Employee Only)
    When the client requests to delete a user by ID
    Then the server should respond with status code 200
    And the response body should contain a success message

  Scenario: Update a user's daily limit (Employee Only)
    And the employee provides a new daily limit
    When the client requests to update the user's daily limit
    Then the server should respond with status code 200
    And the response body should contain the updated daily limit

  Scenario: Update a user's transaction limit (Employee Only)
    And the employee provides a new transaction limit
    When the client requests to update the user's transaction limit
    Then the server should respond with status code 200
    And the response body should contain the updated transaction limit
