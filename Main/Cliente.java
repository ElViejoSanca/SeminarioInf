// Cliente.java
import java.util.ArrayList;
import java.util.List;

public class Cliente extends PersonaSistema {

    private int idCliente;
    private String direccion;
    private String localidad;
    private String email;
    private List<Ticket> historialTickets;

    // Constructor completo
    public Cliente(String nombre, String apellido, String telefono,
                   String direccion, String localidad, String email) {
        super(nombre, apellido, telefono);
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía");
        }
        if (localidad == null || localidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La localidad no puede estar vacía");
        }
        this.idCliente = 0; // Se asignará desde el DAO
        this.direccion = direccion;
        this.localidad = localidad;
        this.email = email;
        this.historialTickets = new ArrayList<>();
    }

    // Constructor sin email (campo opcional)
    public Cliente(String nombre, String apellido, String telefono,
                   String direccion, String localidad) {
        this(nombre, apellido, telefono, direccion, localidad, null);
    }

    // Métodos abstractos implementados
    @Override
    public int getId() { return idCliente; }

    @Override
    public void mostrarInfo() {
        System.out.println("-----------------------------");
        System.out.println("Cliente ID  : " + idCliente);
        System.out.println("Nombre      : " + getNombreCompleto());
        System.out.println("Dirección   : " + direccion + ", " + localidad);
        System.out.println("Teléfono    : " + getTelefono());
        System.out.println("Email       : " + (email != null ? email : "No registrado"));
        System.out.println("Tickets     : " + historialTickets.size());
        System.out.println("-----------------------------");
    }

    // Agrega un ticket al historial del cliente
    public void agregarTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }
        historialTickets.add(ticket);
    }

    public void mostrarHistorialTickets() {
        if (historialTickets.isEmpty()) {
            System.out.println("El cliente no tiene tickets registrados.");
            return;
        }
        System.out.println("Historial de tickets de " + getNombreCompleto() + ":");
        for (Ticket t : historialTickets) {
            t.mostrarResumen();
        }
    }

    // Getters y Setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía");
        }
        this.direccion = direccion;
    }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Ticket> getHistorialTickets() { return historialTickets; }

    @Override
    public String toString() {
        return idCliente + " - " + getNombreCompleto() + " (" + localidad + ")";
    }
}