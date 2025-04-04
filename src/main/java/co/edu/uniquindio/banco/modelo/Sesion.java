package co.edu.uniquindio.banco.modelo;

import co.edu.uniquindio.banco.modelo.entidades.Usuario;
import lombok.Getter;
import lombok.Setter;

/**
 * @author juand
 */
public class Sesion {

    private static Sesion INSTANCIA;

    @Getter @Setter
    private Usuario usuario;

    private Sesion() {
    }

    public static Sesion getInstance() {
        if (INSTANCIA == null) {
            INSTANCIA = new Sesion();
        }
        return INSTANCIA;
    }

    public void cerrarSesion() {
        usuario = null;
    }
}
