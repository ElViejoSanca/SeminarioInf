// ItemInventarioDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemInventarioDAO {

    private SucursalDAO sucursalDAO = new SucursalDAO();

    // Inserta el item generico y su stock inicial en la sucursal indicada
    public void insertar(ItemInventario item) throws PersistenciaException {
        String sqlItem = "INSERT INTO item_inventario (codigo, nombre, descripcion, " +
                         "categoria, unidad_medida) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "INSERT INTO stock (id_item, id_sucursal, cantidad_disponible, " +
                          "stock_minimo) VALUES (?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            con.setAutoCommit(false);

            PreparedStatement psItem = con.prepareStatement(sqlItem, PreparedStatement.RETURN_GENERATED_KEYS);
            psItem.setString(1, item.getCodigo());
            psItem.setString(2, item.getNombre());
            psItem.setString(3, item.getDescripcion());
            psItem.setString(4, item.getCategoria().name());
            psItem.setString(5, item.getUnidadMedida());
            psItem.executeUpdate();

            ResultSet rs = psItem.getGeneratedKeys();
            if (rs.next()) {
                item.setIdItem(rs.getInt(1));
            }
            psItem.close();

            PreparedStatement psStock = con.prepareStatement(sqlStock);
            psStock.setInt(1, item.getIdItem());
            psStock.setInt(2, item.getSucursal().getIdSucursal());
            psStock.setInt(3, item.getCantidadDisponible());
            psStock.setInt(4, item.getStockMinimo());
            psStock.executeUpdate();
            psStock.close();

            con.commit();
            con.setAutoCommit(true);

        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar item de inventario: " + e.getMessage(), e);
        }
    }

    public ItemInventario buscarPorId(int id) throws PersistenciaException {
        String sql = "SELECT i.*, s.cantidad_disponible, s.stock_minimo, s.id_sucursal " +
                     "FROM item_inventario i " +
                     "INNER JOIN stock s ON i.id_item = s.id_item " +
                     "WHERE i.id_item = ? LIMIT 1";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearItem(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar item por id: " + e.getMessage(), e);
        }
    }

    public ItemInventario buscarPorCodigo(String codigo) throws PersistenciaException {
        String sql = "SELECT i.*, s.cantidad_disponible, s.stock_minimo, s.id_sucursal " +
                     "FROM item_inventario i " +
                     "INNER JOIN stock s ON i.id_item = s.id_item " +
                     "WHERE i.codigo = ? LIMIT 1";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearItem(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar item por codigo: " + e.getMessage(), e);
        }
    }

    public List<ItemInventario> listarPorSucursal(int idSucursal) throws PersistenciaException {
        String sql = "SELECT i.*, s.cantidad_disponible, s.stock_minimo, s.id_sucursal " +
                     "FROM item_inventario i " +
                     "INNER JOIN stock s ON i.id_item = s.id_item " +
                     "WHERE s.id_sucursal = ?";
        List<ItemInventario> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearItem(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar items por sucursal: " + e.getMessage(), e);
        }
    }

    public List<ItemInventario> listarStockBajo() throws PersistenciaException {
        String sql = "SELECT i.*, s.cantidad_disponible, s.stock_minimo, s.id_sucursal " +
                     "FROM item_inventario i " +
                     "INNER JOIN stock s ON i.id_item = s.id_item " +
                     "WHERE s.cantidad_disponible < s.stock_minimo";
        List<ItemInventario> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearItem(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar items con stock bajo: " + e.getMessage(), e);
        }
    }

    // Descuenta stock de forma atomica, lanzando excepcion si no hay suficiente
    public void descontarStock(int idItem, int idSucursal, int cantidad) throws PersistenciaException {
        String sqlVerificar = "SELECT cantidad_disponible FROM stock " +
                              "WHERE id_item = ? AND id_sucursal = ? FOR UPDATE";
        String sqlActualizar = "UPDATE stock SET cantidad_disponible = cantidad_disponible - ? " +
                               "WHERE id_item = ? AND id_sucursal = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            con.setAutoCommit(false);

            PreparedStatement psVerificar = con.prepareStatement(sqlVerificar);
            psVerificar.setInt(1, idItem);
            psVerificar.setInt(2, idSucursal);
            ResultSet rs = psVerificar.executeQuery();

            if (!rs.next()) {
                con.rollback();
                con.setAutoCommit(true);
                throw new PersistenciaException("No existe stock registrado para ese item en esa sucursal");
            }

            int disponible = rs.getInt("cantidad_disponible");
            if (cantidad > disponible) {
                con.rollback();
                con.setAutoCommit(true);
                throw new PersistenciaException("Stock insuficiente. Disponible: " + disponible +
                                                ", solicitado: " + cantidad);
            }
            psVerificar.close();

            PreparedStatement psActualizar = con.prepareStatement(sqlActualizar);
            psActualizar.setInt(1, cantidad);
            psActualizar.setInt(2, idItem);
            psActualizar.setInt(3, idSucursal);
            psActualizar.executeUpdate();
            psActualizar.close();

            con.commit();
            con.setAutoCommit(true);

        } catch (SQLException e) {
            throw new PersistenciaException("Error al descontar stock: " + e.getMessage(), e);
        }
    }

    // Convierte una fila del ResultSet en un objeto ItemInventario
    private ItemInventario mapearItem(ResultSet rs) throws SQLException, PersistenciaException {
        Sucursal sucursal = sucursalDAO.buscarPorId(rs.getInt("id_sucursal"));

        ItemInventario item = new ItemInventario(
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            CategoriaItem.valueOf(rs.getString("categoria")),
            rs.getString("unidad_medida"),
            rs.getInt("cantidad_disponible"),
            rs.getInt("stock_minimo"),
            sucursal
        );
        item.setIdItem(rs.getInt("id_item"));
        return item;
    }

    public List<ItemInventario> listarTodos() throws PersistenciaException {
    String sql = "SELECT i.*, s.cantidad_disponible, s.stock_minimo, s.id_sucursal " +
                 "FROM item_inventario i " +
                 "INNER JOIN stock s ON i.id_item = s.id_item";
    List<ItemInventario> resultado = new ArrayList<>();
    try {
        Connection con = ConexionBD.getInstancia().getConexion();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            resultado.add(mapearItem(rs));
        }
        return resultado;
    } catch (SQLException e) {
        throw new PersistenciaException("Error al listar todos los items: " + e.getMessage(), e);
    }
}
}