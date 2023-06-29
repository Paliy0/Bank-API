#Feature: User Management
#
#  Background:
#    Given the user has logged in as a normal user
#
#  Scenario: Change user details
#    Given the user is logged in and provides data to be changed
#    When the user requests to change the data
#    Then the server should respond with status code 200
#
#  Scenario: Get transaction limit
#    When the client requests to get the transaction limit
#    Then the server should respond with status code 200
#    And the server responds with the transaction limit
#
#  Scenario: Get daily limit
#    When the client requests to get the daily limit
#    Then the server should respond with status code 200
#    And the server responds with the daily limit