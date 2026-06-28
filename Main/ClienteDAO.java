// ClienteDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void insertar(Cliente cliente) throws PersistenciaException {
        String sql = "INSERT INTO cliente (nombre, apellido, direccion, localidad, " +
                     "telefono, email) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDireccion());
            ps.setString(4, cliente.getLocalidad());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                cliente.setIdCliente(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorId(int id) throws PersistenciaException {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar cliente por id: " + e.getMessage(), e);
        }
    }

    public List<Cliente> listarTodos() throws PersistenciaException {
        String sql = "SELECT * FROM cliente ORDER BY apellido, nombre";
        List<Cliente> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearCliente(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar clientes: " + e.getMessage(), e);
        }
    }

    public List<Cliente> buscarPorNombre(String texto) throws PersistenciaException {
        String sql = "SELECT * FROM cliente WHERE nombre LIKE ? OR apellido LIKE ?";
        List<Cliente> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearCliente(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar cliente por nombre: " + e.getMessage(), e);
        }
    }

    public void actualizar(Cliente cliente) throws PersistenciaException {
        String sql = "UPDATE cliente SET nombre = ?, apellido = ?, direccion = ?, " +
                     "localidad = ?, telefono = ?, email = ? WHERE id_cliente = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDireccion());
            ps.setString(4, cliente.getLocalidad());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.setInt(7, cliente.getIdCliente());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) throws PersistenciaException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar cliente: " + e.getMessage(), e);
        }
    }

    // Convierte una fila del ResultSet en un objeto Cliente
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("telefono"),
            rs.getString("direccion"),
            rs.getString("localidad"),
            rs.getString("email")
        );
        cliente.setIdCliente(rs.getInt("id_cliente"));
        return cliente;
    }
}