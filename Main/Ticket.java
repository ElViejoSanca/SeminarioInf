// Ticket.java
public class Ticket {

    private int idTicket;
    private Cliente cliente;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoTicket estado;
    private String areaDestino;

    // Constructor completo
    public Ticket(Cliente cliente, String descripcion, Prioridad prioridad, String areaDestino) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        this.idTicket = 0; // Se asignará desde el DAO
        this.cliente = cliente;
        this.descripcion = descripcion;
        this.prioridad = prioridad != null ? prioridad : Prioridad.MEDIA;
        this.estado = EstadoTicket.PENDIENTE;
        this.areaDestino = areaDestino != null ? areaDestino : "CAT";
    }

    // Constructor con prioridad y área por defecto
    public Ticket(Cliente cliente, String descripcion) {
        this(cliente, descripcion, Prioridad.MEDIA, "CAT");
    }

    // Crea un ticket y lo registra en el historial del cliente después de su construccion
    public static Ticket crearTicket(Cliente cliente, String descripcion, Prioridad prioridad, String areaDestino) {
        Ticket ticket = new Ticket(cliente, descripcion, prioridad, areaDestino);
        cliente.agregarTicket(ticket);
        return ticket;
    }

    public static Ticket crearTicket(Cliente cliente, String descripcion) {
        return crearTicket(cliente, descripcion, Prioridad.MEDIA, "CAT");
    }

    // Intenta convertir el ticket en orden de servicio
    public OrdenServicio convertirAOrden(TipoServicio tipoServicio,
                                          Prioridad prioridadOrden,
                                          Sucursal sucursal,
                                          String observaciones) {
        if (this.estado == EstadoTicket.CONVERTIDO) {
            throw new IllegalStateException("El ticket " + idTicket +
                                            " ya fue convertido en orden de servicio");
        }
        if (this.estado == EstadoTicket.RESUELTO_DIRECTO) {
            throw new IllegalStateException("El ticket " + idTicket +
                                            " ya fue resuelto directamente, no puede convertirse");
        }
        if (sucursal == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula");
        }

        OrdenServicio orden = new OrdenServicio(this, tipoServicio, prioridadOrden,
                                                sucursal, observaciones);
        this.estado = EstadoTicket.CONVERTIDO;
        return orden;
    }

    public void mostrarResumen() {
        System.out.println("  Ticket #" + idTicket +
                           " | " + prioridad +
                           " | " + estado +
                           " | " + descripcion.substring(0, Math.min(descripcion.length(), 50)));
    }

    public void mostrarInfo() {
        System.out.println("-----------------------------");
        System.out.println("Ticket ID   : " + idTicket);
        System.out.println("Cliente     : " + cliente.getNombreCompleto());
        System.out.println("Descripción : " + descripcion);
        System.out.println("Prioridad   : " + prioridad);
        System.out.println("Estado      : " + estado);
        System.out.println("Área destino: " + areaDestino);
        System.out.println("-----------------------------");
    }

    // Getters y Setters
    public int getIdTicket() { return idTicket; }
    public void setIdTicket(int idTicket) { this.idTicket = idTicket; }

    public Cliente getCliente() { return cliente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public EstadoTicket getEstado() { return estado; }
    public void setEstado(EstadoTicket estado) { this.estado = estado; }

    public String getAreaDestino() { return areaDestino; }
    public void setAreaDestino(String areaDestino) { this.areaDestino = areaDestino; }

    @Override
    public String toString() {
        return idTicket + " - " + cliente.getNombreCompleto() +
               " | " + prioridad + " | " + estado;
    }
}