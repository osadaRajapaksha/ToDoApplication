package com.lesstaxi.todoapp.payload.response;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String id;
  private String username;
  private String role;

  public JwtResponse(String accessToken, String id, String username, String role) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.role = role;
  }

  public String getAccessToken() { return token; }
  public void setAccessToken(String accessToken) { this.token = accessToken; }
  public String getTokenType() { return type; }
  public void setTokenType(String tokenType) { this.type = tokenType; }
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
}
