package com.ispc.manoamano.modelos;

public class ContactoRequest {

    private final String nombre;
    private final String email;
    private final String asunto;
    private final String mensaje;

    public ContactoRequest(String nombre, String email, String asunto, String mensaje) {
        this.nombre = nombre;
        this.email = email;
        this.asunto = asunto;
        this.mensaje = mensaje;
    }
}
