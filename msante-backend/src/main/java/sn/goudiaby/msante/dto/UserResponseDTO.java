package sn.goudiaby.msante.dto;

public class UserResponseDTO {
    
    private Long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String userType;
    private Boolean isActive;
    private String createdAt;

    // Default constructor
    public UserResponseDTO() {}

    // Manual constructor to ensure compilation works
    public UserResponseDTO(Long userId, String firstName, String lastName, String phone, 
                          String email, String userType, Boolean isActive, String createdAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.userType = userType;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Manual getters and setters to ensure compilation works
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}