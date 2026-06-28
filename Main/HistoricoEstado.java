// HistoricoEstado.java
public class HistoricoEstado {

    private int idHistorico;
    private int idOrden;
    private String estadoAnterior;
    private String estadoNuevo;
    private int idUsuario;
    private String nombreUsuario;
    private String observaciones;
    private String fechaCambio;

    // Constructor para registrar un nuevo cambio (antes de persistir)
    public HistoricoEstado(int idOrden, String estadoAnterior, String estadoNuevo,
                           int idUsuario, String observaciones) {
        if (estadoNuevo == null || estadoNuevo.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado nuevo no puede estar vacio");
        }
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El usuario responsable es invalido");
        }
        this.idHistorico = 0;
        this.idOrden = idOrden;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.idUsuario = idUsuario;
        this.observaciones = observaciones;
    }

    public void mostrarInfo() {
        System.out.println("  " + (fechaCambio != null ? fechaCambio : "Sin fecha") +
                           " | " + (estadoAnterior != null ? estadoAnterior : "INICIAL") +
                           " -> " + estadoNuevo +
                           " | " + (nombreUsuario != null ? nombreUsuario : "Usuario #" + idUsuario) +
                           (observaciones != null && !observaciones.isEmpty() ?
                            " | " + observaciones : ""));
    }

    // Getters y Setters
    public int getIdHistorico() { return idHistorico; }
    public void setIdHistorico(int idHistorico) { this.idHistorico = idHistorico; }

    public int getIdOrden() { return idOrden; }

    public String getEstadoAnterior() { return estadoAnterior; }

    public String getEstadoNuevo() { return estadoNuevo; }

    public int getIdUsuario() { return idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getObservaciones() { return observaciones; }

    public String getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(String fechaCambio) { this.fechaCambio = fechaCambio; }

    @Override
    public String toString() {
        return (estadoAnterior != null ? estadoAnterior : "INICIAL") +
               " -> " + estadoNuevo + " (" + (fechaCambio != null ? fechaCambio : "pendiente") + ")";
    }
}