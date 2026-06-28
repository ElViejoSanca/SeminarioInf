// UsoEquipamientoDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsoEquipamientoDAO {

    private ItemInventarioDAO itemDAO = new ItemInventarioDAO();

    public void insertar(int idOrden, UsoEquipamiento uso) throws PersistenciaException {
        String sql = "INSERT INTO uso_equipamiento (id_orden, id_item, cantidad_utilizada) " +
                     "VALUES (?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idOrden);
            ps.setInt(2, uso.getItem().getIdItem());
            ps.setInt(3, uso.getCantidadUtilizada());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar uso de equipamiento: " + e.getMessage(), e);
        }
    }

    public List<UsoEquipamiento> listarPorOrden(int idOrden) throws PersistenciaException {
        String sql = "SELECT * FROM uso_equipamiento WHERE id_orden = ?";
        List<UsoEquipamiento> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idOrden);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ItemInventario item = itemDAO.buscarPorId(rs.getInt("id_item"));
                UsoEquipamiento uso = new UsoEquipamiento(item, rs.getInt("cantidad_utilizada"));
                resultado.add(uso);
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar uso de equipamiento: " + e.getMessage(), e);
        }
    }
}