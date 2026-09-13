package com.lesstaxi.todoapp.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
  @Id
  private String id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 120)
  private String password;

  private ERole role = ERole.ROLE_USER;

  public User() {
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  public ERole getRole() { return role; }
  public void setRole(ERole role) { this.role = role; }
}
