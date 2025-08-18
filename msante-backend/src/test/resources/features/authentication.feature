Feature: Authentication API
  As a user of the M-Santé system
  I want to be able to authenticate via OAuth2/JWT
  So that I can access protected endpoints

  Background:
    Given the M-Santé application is running

  Scenario: Successful user registration
    When I register a new patient user with email "patient@test.com" and password "password123"
    Then the registration should be successful
    And I should receive a confirmation message

  Scenario: Successful user login with JWT token
    Given a patient user exists with email "existing@test.com" and password "password123"
    When I login with email "existing@test.com" and password "password123"
    Then the login should be successful
    And I should receive a JWT token in the response header

  Scenario: Access protected endpoint with valid JWT token
    Given a patient user exists with email "patient@test.com" and password "password123"
    And I have a valid JWT token for that user
    When I access the patient appointments endpoint
    Then I should receive a successful response

  Scenario: Access protected endpoint without JWT token
    When I access the patient appointments endpoint without authentication
    Then I should receive an unauthorized response

  Scenario: Access protected endpoint with invalid JWT token
    When I access the patient appointments endpoint with an invalid JWT token
    Then I should receive an unauthorized response

  Scenario: Access doctor endpoint as patient (role-based access)
    Given a patient user exists with email "patient@test.com" and password "password123"
    And I have a valid JWT token for that user
    When I access the doctor appointments endpoint
    Then I should receive a forbidden response

  Scenario: Successful appointment booking with JWT authentication
    Given a doctor user exists with email "doctor@test.com" and password "password123"
    And a patient user exists with email "patient@test.com" and password "password123"
    And I have a valid JWT token for the patient
    And there is an available appointment slot
    When I book an appointment
    Then the booking should be successful
    And I should receive appointment confirmation details