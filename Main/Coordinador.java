// Coordinador.java
public class Coordinador extends UsuarioSistema {

    // Constructor
    public Coordinador(String nombre, String apellido, String telefono,
                       String username, String password, String email) {
        super(nombre, apellido, telefono, username, password, "COORDINADOR_CAT", email);
    }

    @Override
    public void mostrarInfo() {
        System.out.println("-----------------------------");
        System.out.println("Coordinador ID : " + getId());
        System.out.println("Nombre         : " + getNombreCompleto());
        System.out.println("Username       : " + getUsername());
        System.out.println("Rol            : " + getRol());
        System.out.println("Estado         : " + (isActivo() ? "Activo" : "Inactivo"));
        System.out.println("-----------------------------");
    }

    // Audita y cierra formalmente una orden pendiente de auditoría
    public boolean auditarOrden(OrdenServicio orden, String observacionFinal) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula");
        }
        if (orden.getEstadoActual() != EstadoOrden.PENDIENTE_AUDITORIA) {
            System.out.println("La orden " + orden.getNumeroOrden() +
                               " no está pendiente de auditoría. Estado actual: " +
                               orden.getEstadoActual());
            return false;
        }
        orden.cambiarEstado(EstadoOrden.CERRADA);
        if (observacionFinal != null && !observacionFinal.trim().isEmpty()) {
            orden.setObservaciones(observacionFinal);
        }
        System.out.println("Orden " + orden.getNumeroOrden() +
                           " auditada y cerrada correctamente por " + getNombreCompleto());
        return true;
    }

    // Rechaza el cierre de una orden y la devuelve al técnico
    public boolean rechazarCierreOrden(OrdenServicio orden, String motivoRechazo) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula");
        }
        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar un motivo de rechazo");
        }
        if (orden.getEstadoActual() != EstadoOrden.PENDIENTE_AUDITORIA) {
            System.out.println("La orden no está pendiente de auditoría.");
            return false;
        }
        orden.cambiarEstado(EstadoOrden.ASIGNADA);
        orden.setObservaciones("RECHAZO: " + motivoRechazo);
        System.out.println("Cierre rechazado. Orden " + orden.getNumeroOrden() +
                           " devuelta al técnico. Motivo: " + motivoRechazo);
        return true;
    }

    @Override
    public String toString() {
        return getId() + " - " + getNombreCompleto() + " [" + getRol() + "]";
    }
}