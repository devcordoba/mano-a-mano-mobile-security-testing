package com.ispc.manoamano.modelos;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    private int id;

    private String username;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    private String email;

    private Perfil perfil;

    private Organizacion organizacion;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }
}
