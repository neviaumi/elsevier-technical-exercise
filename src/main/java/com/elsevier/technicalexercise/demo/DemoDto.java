package com.elsevier.technicalexercise.demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for demo requests.
 * Contains identification, name, and contact information.
 */
public class DemoDto {

  @NotNull(message = "id must not be null")
  @NotBlank(message = "id must not be blank")
  private String id;

  @NotBlank(message = "name must not be blank")
  @Size(max = 100, message = "name must be less than 100 characters")
  private String name;

  @NotBlank(message = "email must not be blank")
  @Email(message = "email must be a valid email address")
  private String email;

  // getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
