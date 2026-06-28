// SucursalDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {

    public void insertar(Sucursal sucursal) throws PersistenciaException {
        String sql = "INSERT INTO sucursal (nombre, localidad, direccion, telefono, activa) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getLocalidad());
            ps.setString(3, sucursal.getDireccion());
            ps.setString(4, sucursal.getTelefono());
            ps.setBoolean(5, sucursal.isActiva());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                sucursal.setIdSucursal(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar sucursal: " + e.getMessage(), e);
        }
    }

    public Sucursal buscarPorId(int id) throws PersistenciaException {
        String sql = "SELECT * FROM sucursal WHERE id_sucursal = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearSucursal(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar sucursal por id: " + e.getMessage(), e);
        }
    }

    public List<Sucursal> listarTodas() throws PersistenciaException {
        String sql = "SELECT * FROM sucursal WHERE activa = TRUE ORDER BY id_sucursal";
        List<Sucursal> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearSucursal(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar sucursales: " + e.getMessage(), e);
        }
    }

    public void actualizar(Sucursal sucursal) throws PersistenciaException {
        String sql = "UPDATE sucursal SET nombre = ?, localidad = ?, direccion = ?, " +
                     "telefono = ?, activa = ? WHERE id_sucursal = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getLocalidad());
            ps.setString(3, sucursal.getDireccion());
            ps.setString(4, sucursal.getTelefono());
            ps.setBoolean(5, sucursal.isActiva());
            ps.setInt(6, sucursal.getIdSucursal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar sucursal: " + e.getMessage(), e);
        }
    }

    // Convierte una fila del ResultSet en un objeto Sucursal
    private Sucursal mapearSucursal(ResultSet rs) throws SQLException {
        Sucursal s = new Sucursal(
            rs.getString("nombre"),
            rs.getString("localidad"),
            rs.getString("direccion"),
            rs.getString("telefono")
        );
        s.setIdSucursal(rs.getInt("id_sucursal"));
        s.setActiva(rs.getBoolean("activa"));
        return s;
    }
}