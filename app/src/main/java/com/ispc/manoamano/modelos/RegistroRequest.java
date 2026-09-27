package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class RegistroRequest {

    private String username;

    private String email;

    private String password;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    public RegistroRequest(
            String username,
            String email,
            String password,
            String firstName,
            String lastName
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}