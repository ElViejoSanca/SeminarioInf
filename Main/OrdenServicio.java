// OrdenServicio.java
import java.util.ArrayList;
import java.util.List;

public class OrdenServicio {

    private int idOrden;
    private String numeroOrden;
    private Ticket ticket;
    private Tecnico tecnico;
    private TipoServicio tipoServicio;
    private Prioridad prioridad;
    private EstadoOrden estadoActual;
    private Sucursal sucursal;
    private String fechaCoordinada;
    private String observaciones;
    private String resolucion;
    private List<UsoEquipamiento> equipamientoUsado;

    // Constructor
    public OrdenServicio(Ticket ticket, TipoServicio tipoServicio,
                         Prioridad prioridad, Sucursal sucursal,
                         String observaciones) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }
        if (tipoServicio == null) {
            throw new IllegalArgumentException("El tipo de servicio no puede ser nulo");
        }
        if (sucursal == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula");
        }
        this.idOrden = 0;
        this.numeroOrden = null;
        this.ticket = ticket;
        this.tipoServicio = tipoServicio;
        this.prioridad = prioridad != null ? prioridad : Prioridad.MEDIA;
        this.estadoActual = EstadoOrden.SIN_ASIGNAR;
        this.sucursal = sucursal;
        this.observaciones = observaciones;
        this.equipamientoUsado = new ArrayList<>();
    }

    // Asigna técnico a la orden validando estado y sucursal
    public void asignarTecnico(Tecnico tecnico, String fechaCoordinada) {
        if (tecnico == null) {
            throw new IllegalArgumentException("El técnico no puede ser nulo");
        }
        if (fechaCoordinada == null || fechaCoordinada.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha coordinada no puede estar vacía");
        }
        if (this.estadoActual != EstadoOrden.SIN_ASIGNAR &&
            this.estadoActual != EstadoOrden.POSPUESTA) {
            throw new IllegalStateException("La orden " + numeroOrden +
                                            " no puede ser asignada en su estado actual: " +
                                            estadoActual);
        }
        this.tecnico = tecnico;
        this.fechaCoordinada = fechaCoordinada;
        this.estadoActual = EstadoOrden.ASIGNADA;
        tecnico.agregarOrden(this);
        System.out.println("Orden " + numeroOrden + " asignada a " +
                           tecnico.getNombreCompleto() + " para el " + fechaCoordinada);
    }

    // Cambia el estado de la orden validando transiciones permitidas
    public void cambiarEstado(EstadoOrden nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        if (!transicionValida(this.estadoActual, nuevoEstado)) {
            throw new IllegalStateException("Transición de estado inválida: " +
                                            estadoActual + " -> " + nuevoEstado);
        }
        System.out.println("Orden " + numeroOrden + ": " +
                           estadoActual + " -> " + nuevoEstado);
        this.estadoActual = nuevoEstado;
    }

    // Valida si una transición de estado es lógicamente permitida
    private boolean transicionValida(EstadoOrden actual, EstadoOrden nuevo) {
        return switch (actual) {
            case SIN_ASIGNAR -> nuevo == EstadoOrden.ASIGNADA;
            case ASIGNADA -> nuevo == EstadoOrden.CONFIRMADA ||
                nuevo == EstadoOrden.POSPUESTA;
            case CONFIRMADA -> nuevo == EstadoOrden.EN_CAMINO ||
                nuevo == EstadoOrden.POSPUESTA;
            case EN_CAMINO -> nuevo == EstadoOrden.EN_SITIO ||
                nuevo == EstadoOrden.POSPUESTA;
            case EN_SITIO -> nuevo == EstadoOrden.RESUELTA ||
                nuevo == EstadoOrden.POSPUESTA;
            case RESUELTA -> nuevo == EstadoOrden.PENDIENTE_AUDITORIA;
            case PENDIENTE_AUDITORIA -> nuevo == EstadoOrden.CERRADA ||
                nuevo == EstadoOrden.ASIGNADA;
            case POSPUESTA -> nuevo == EstadoOrden.ASIGNADA;
            case CERRADA -> false;
            default -> false;
        };
    }

    // Registra la resolución y descuenta inventario automáticamente
    public void registrarResolucion(String resolucion, List<UsoEquipamiento> equipamiento) {
        if (resolucion == null || resolucion.trim().isEmpty()) {
            throw new IllegalArgumentException("La resolución no puede estar vacía");
        }
        if (this.estadoActual != EstadoOrden.EN_SITIO &&
            this.estadoActual != EstadoOrden.RESUELTA) {
            throw new IllegalStateException("No se puede registrar resolución en estado: " +
                                            estadoActual);
        }
        this.resolucion = resolucion;

        // Descuenta stock de cada item utilizado
        if (equipamiento != null && !equipamiento.isEmpty()) {
            for (UsoEquipamiento uso : equipamiento) {
                try {
                    uso.getItem().descontarStock(uso.getCantidadUtilizada());
                    this.equipamientoUsado.add(uso);
                    System.out.println("Stock descontado: " + uso);
                } catch (IllegalStateException e) {
                    System.out.println("Advertencia - " + e.getMessage());
                    this.equipamientoUsado.add(uso);
                }
            }
        }
        this.estadoActual = EstadoOrden.PENDIENTE_AUDITORIA;
        System.out.println("Resolución registrada. Orden " + numeroOrden +
                           " pendiente de auditoría.");
    }

    public void mostrarResumen() {
        System.out.println("  Orden " + numeroOrden +
                           " | " + tipoServicio +
                           " | " + prioridad +
                           " | " + estadoActual +
                           " | Técnico: " + (tecnico != null ?
                           tecnico.getNombreCompleto() : "Sin asignar"));
    }

    public void mostrarInfo() {
        System.out.println("=============================");
        System.out.println("Orden       : " + numeroOrden);
        System.out.println("Ticket ID   : " + ticket.getIdTicket());
        System.out.println("Cliente     : " + ticket.getCliente().getNombreCompleto());
        System.out.println("Dirección   : " + ticket.getCliente().getDireccion());
        System.out.println("Tipo        : " + tipoServicio);
        System.out.println("Prioridad   : " + prioridad);
        System.out.println("Estado      : " + estadoActual);
        System.out.println("Sucursal    : " + sucursal.getNombre());
        System.out.println("Técnico     : " + (tecnico != null ?
                           tecnico.getNombreCompleto() : "Sin asignar"));
        System.out.println("Fecha coord.: " + (fechaCoordinada != null ?
                           fechaCoordinada : "Sin coordinar"));
        System.out.println("Observación : " + (observaciones != null ?
                           observaciones : "-"));
        System.out.println("Resolución  : " + (resolucion != null ?
                           resolucion : "Sin registrar"));
        if (!equipamientoUsado.isEmpty()) {
            System.out.println("Equipamiento utilizado:");
            for (UsoEquipamiento uso : equipamientoUsado) {
                uso.mostrarInfo();
            }
        }
        System.out.println("=============================");
    }

    // Getters y Setters
    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }
    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public Ticket getTicket() { return ticket; }
    public Tecnico getTecnico() { return tecnico; }
    public TipoServicio getTipoServicio() { return tipoServicio; }
    public Prioridad getPrioridad() { return prioridad; }
    public EstadoOrden getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoOrden estadoActual) { this.estadoActual = estadoActual; }
    public void setTecnico(Tecnico tecnico) { this.tecnico = tecnico; }
    public void setFechaCoordinada(String fechaCoordinada) { this.fechaCoordinada = fechaCoordinada; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    public Sucursal getSucursal() { return sucursal; }
    public String getFechaCoordinada() { return fechaCoordinada; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getResolucion() { return resolucion; }
    public List<UsoEquipamiento> getEquipamientoUsado() { return equipamientoUsado; }

    @Override
    public String toString() {
        return numeroOrden + " | " + tipoServicio + " | " + prioridad +
               " | " + estadoActual + " | " +
               (tecnico != null ? tecnico.getNombreCompleto() : "Sin asignar");
    }
}