// ItemInventario.java
public class ItemInventario {

    private int idItem;
    private String codigo;
    private String nombre;
    private String descripcion;
    private CategoriaItem categoria;
    private String unidadMedida;
    private int cantidadDisponible;
    private int stockMinimo;
    private Sucursal sucursal;

    // Constructor completo
    public ItemInventario(String codigo, String nombre, String descripcion,
                          CategoriaItem categoria, String unidadMedida,
                          int cantidadDisponible, int stockMinimo, Sucursal sucursal) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (cantidadDisponible < 0) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser negativa");
        }
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        if (sucursal == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula");
        }
        this.idItem = 0; // Se asignará más adelante si es necesario
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria != null ? categoria : CategoriaItem.OTRO;
        this.unidadMedida = unidadMedida != null ? unidadMedida : "unidad";
        this.cantidadDisponible = cantidadDisponible;
        this.stockMinimo = stockMinimo;
        this.sucursal = sucursal;
    }

    // Constructor simplificado sin descripcion
    public ItemInventario(String codigo, String nombre, CategoriaItem categoria,
                          int cantidadDisponible, int stockMinimo, Sucursal sucursal) {
        this(codigo, nombre, null, categoria, "unidad",
             cantidadDisponible, stockMinimo, sucursal);
    }

    // Descuenta cantidad del stock, lanza excepción si no hay suficiente
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero");
        }
        if (cantidad > cantidadDisponible) {
            throw new IllegalStateException("Stock insuficiente para " + nombre +
                                            ". Disponible: " + cantidadDisponible +
                                            ", solicitado: " + cantidad);
        }
        this.cantidadDisponible -= cantidad;
    }

    // Repone cantidad al stock
    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reponer debe ser mayor a cero");
        }
        this.cantidadDisponible += cantidad;
    }

    // Verifica si el stock está por debajo del mínimo
    public boolean verificarStockMinimo() {
        return cantidadDisponible < stockMinimo;
    }

    public void mostrarInfo() {
        System.out.println("-----------------------------");
        System.out.println("Item ID     : " + idItem);
        System.out.println("Código      : " + codigo);
        System.out.println("Nombre      : " + nombre);
        System.out.println("Categoría   : " + categoria);
        System.out.println("Sucursal    : " + sucursal.getNombre());
        System.out.println("Stock actual: " + cantidadDisponible + " " + unidadMedida);
        System.out.println("Stock mínimo: " + stockMinimo + " " + unidadMedida);
        System.out.println("Alerta      : " + (verificarStockMinimo() ? "STOCK BAJO" : "OK"));
        System.out.println("-----------------------------");
    }

    // Getters y Setters
    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        this.codigo = codigo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public CategoriaItem getCategoria() { return categoria; }
    public void setCategoria(CategoriaItem categoria) { this.categoria = categoria; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public int getCantidadDisponible() { return cantidadDisponible; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) {
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        this.stockMinimo = stockMinimo;
    }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) {
        if (sucursal == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula");
        }
        this.sucursal = sucursal;
    }
    

    @Override
    public String toString() {
        return idItem + " - " + nombre + " (" + codigo + ")" +
               " | Stock: " + cantidadDisponible + " " + unidadMedida +
               (verificarStockMinimo() ? " [STOCK BAJO]" : "");
    }
}