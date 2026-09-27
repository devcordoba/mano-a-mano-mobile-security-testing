package com.ispc.manoamano.modelos;

public class PerfilResponse {

    private Usuario user;

    public Usuario getUser() {
        return user;
    }

    public Organizacion getOrganizacion() {
        return user == null ? null : user.getOrganizacion();
    }
}
