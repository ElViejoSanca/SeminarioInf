// Tecnico.java
import java.util.ArrayList;
import java.util.List;

public class Tecnico extends UsuarioSistema {

    private String especializacion;
    private Sucursal sucursal;
    private List<OrdenServicio> ordenesAsignadas;

    // Constructor completo
    public Tecnico(String nombre, String apellido, String telefono,
                   String username, String password,
                   String especializacion, Sucursal sucursal, String email) {
        super(nombre, apellido, telefono, username, password, "TECNICO", email);
        if (sucursal == null) {
            throw new IllegalArgumentException("El técnico debe pertenecer a una sucursal");
        }
        this.especializacion = especializacion;
        this.sucursal = sucursal;
        this.ordenesAsignadas = new ArrayList<>();
    }

    @Override
    public void mostrarInfo() {
        System.out.println("-----------------------------");
        System.out.println("Técnico ID      : " + getId());
        System.out.println("Nombre          : " + getNombreCompleto());
        System.out.println("Username        : " + getUsername());
        System.out.println("Especialización : " + especializacion);
        System.out.println("Sucursal        : " + sucursal.getNombre());
        System.out.println("Órdenes activas : " + getOrdenesActivas());
        System.out.println("Estado          : " + (isActivo() ? "Activo" : "Inactivo"));
        System.out.println("-----------------------------");
    }

    // Cuenta órdenes que no están cerradas ni pospuestas
    public int getOrdenesActivas() {
        int count = 0;
        for (OrdenServicio o : ordenesAsignadas) {
            EstadoOrden estado = o.getEstadoActual();
            if (estado != EstadoOrden.CERRADA && estado != EstadoOrden.POSPUESTA) {
                count++;
            }
        }
        return count;
    }

    public void agregarOrden(OrdenServicio orden) {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula");
        }
        ordenesAsignadas.add(orden);
    }

    public void mostrarOrdenesAsignadas() {
        if (ordenesAsignadas.isEmpty()) {
            System.out.println("El técnico no tiene órdenes asignadas.");
            return;
        }
        System.out.println("Órdenes asignadas a " + getNombreCompleto() + ":");
        for (OrdenServicio o : ordenesAsignadas) {
            o.mostrarResumen();
        }
    }

    // Getters y Setters
    public String getEspecializacion() { return especializacion; }
    public void setEspecializacion(String especializacion) {
        this.especializacion = especializacion;
    }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) {
        if (sucursal == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula");
        }
        this.sucursal = sucursal;
    }

    public List<OrdenServicio> getOrdenesAsignadas() { return ordenesAsignadas; }

    @Override
    public String toString() {
        return getId() + " - " + getNombreCompleto() +
               " [" + especializacion + "] - " + sucursal.getNombre() +
               " (Activas: " + getOrdenesActivas() + ")";
    }
}