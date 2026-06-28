// Sucursal.java
public class Sucursal {

    private int idSucursal;
    private String nombre;
    private String localidad;
    private String direccion;
    private String telefono;
    private boolean activa;

    // Constructor completo
    public Sucursal(String nombre, String localidad, String direccion, String telefono) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sucursal no puede estar vacío");
        }
        if (localidad == null || localidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La localidad no puede estar vacía");
        }
        this.idSucursal = 0;
        this.nombre = nombre;
        this.localidad = localidad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activa = true;
    }

    public void mostrarInfo() {
        System.out.println("-----------------------------");
        System.out.println("Sucursal ID : " + idSucursal);
        System.out.println("Nombre      : " + nombre);
        System.out.println("Localidad   : " + localidad);
        System.out.println("Dirección   : " + direccion);
        System.out.println("Teléfono    : " + telefono);
        System.out.println("Estado      : " + (activa ? "Activa" : "Inactiva"));
        System.out.println("-----------------------------");
    }

    // Getters y Setters
    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() {
        return idSucursal + " - " + nombre + " (" + localidad + ")";
    }
}