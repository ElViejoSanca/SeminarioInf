// UsoEquipamiento.java
public class UsoEquipamiento {

    private ItemInventario item;
    private int cantidadUtilizada;

    // Constructor
    public UsoEquipamiento(ItemInventario item, int cantidadUtilizada) {
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo");
        }
        if (cantidadUtilizada <= 0) {
            throw new IllegalArgumentException("La cantidad utilizada debe ser mayor a cero");
        }
        this.item = item;
        this.cantidadUtilizada = cantidadUtilizada;
    }

    public void mostrarInfo() {
        System.out.println("  Item    : " + item.getNombre() +
                           " (" + item.getCodigo() + ")");
        System.out.println("  Cantidad: " + cantidadUtilizada +
                           " " + item.getUnidadMedida());
    }

    // Getters
    public ItemInventario getItem() { return item; }

    public int getCantidadUtilizada() { return cantidadUtilizada; }
    public void setCantidadUtilizada(int cantidadUtilizada) {
        if (cantidadUtilizada <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.cantidadUtilizada = cantidadUtilizada;
    }

    @Override
    public String toString() {
        return item.getNombre() + " x" + cantidadUtilizada +
               " " + item.getUnidadMedida();
    }
}