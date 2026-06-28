// TecnicoDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TecnicoDAO {

    private SucursalDAO sucursalDAO = new SucursalDAO();

    public void insertar(Tecnico tecnico) throws PersistenciaException {
        String sql = "INSERT INTO usuario (username, password_hash, nombre_completo, " +
                     "rol, email, activo) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlTecnico = "INSERT INTO tecnico (id_tecnico, id_sucursal, nombre_completo, " +
                            "especializacion, telefono, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            con.setAutoCommit(false);

            // Inserta primero en usuario para obtener el id generado
            PreparedStatement psUsuario = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            psUsuario.setString(1, tecnico.getUsername());
            psUsuario.setString(2, tecnico.getPassword());
            psUsuario.setString(3, tecnico.getNombreCompleto());
            psUsuario.setString(4, "TECNICO");
            psUsuario.setString(5, tecnico.getEmail());
            psUsuario.setBoolean(6, tecnico.isActivo());
            psUsuario.executeUpdate();

            ResultSet rs = psUsuario.getGeneratedKeys();
            if (rs.next()) {
                tecnico.setIdUsuario(rs.getInt(1));
            }
            psUsuario.close();

            // Inserta en tecnico usando el mismo id generado
            PreparedStatement psTecnico = con.prepareStatement(sqlTecnico);
            psTecnico.setInt(1, tecnico.getIdUsuario());
            psTecnico.setInt(2, tecnico.getSucursal().getIdSucursal());
            psTecnico.setString(3, tecnico.getNombreCompleto());
            psTecnico.setString(4, tecnico.getEspecializacion());
            psTecnico.setString(5, tecnico.getTelefono());
            psTecnico.setBoolean(6, tecnico.isActivo());
            psTecnico.executeUpdate();
            psTecnico.close();

            con.commit();
            con.setAutoCommit(true);

        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar tecnico: " + e.getMessage(), e);
        }
    }

    public Tecnico buscarPorId(int id) throws PersistenciaException {
        String sql = "SELECT t.id_tecnico, t.especializacion, t.id_sucursal, " +
                     "u.username, u.password_hash, u.nombre_completo, t.telefono, u.email, u.activo " +
                     "FROM tecnico t INNER JOIN usuario u ON t.id_tecnico = u.id_usuario " +
                     "WHERE t.id_tecnico = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearTecnico(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar tecnico por id: " + e.getMessage(), e);
        }
    }

    public List<Tecnico> listarPorSucursal(int idSucursal) throws PersistenciaException {
        String sql = "SELECT t.id_tecnico, t.especializacion, t.id_sucursal, " +
                     "u.username, u.password_hash, u.nombre_completo, t.telefono, u.email, u.activo " +
                     "FROM tecnico t INNER JOIN usuario u ON t.id_tecnico = u.id_usuario " +
                     "WHERE t.id_sucursal = ? AND u.activo = TRUE";
        List<Tecnico> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idSucursal);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearTecnico(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar tecnicos por sucursal: " + e.getMessage(), e);
        }
    }

    public List<Tecnico> listarTodos() throws PersistenciaException {
        String sql = "SELECT t.id_tecnico, t.especializacion, t.id_sucursal, " +
                     "u.username, u.password_hash, u.nombre_completo, t.telefono, u.email, u.activo " +
                     "FROM tecnico t INNER JOIN usuario u ON t.id_tecnico = u.id_usuario " +
                     "WHERE u.activo = TRUE";
        List<Tecnico> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearTecnico(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar tecnicos: " + e.getMessage(), e);
        }
    }

    public List<Tecnico> buscarPorNombre(String nombre) throws PersistenciaException {
        String sql = "SELECT t.id_tecnico, t.especializacion, t.id_sucursal, " +
                     "u.username, u.password_hash, u.nombre_completo, t.telefono, u.email, u.activo " +
                     "FROM tecnico t INNER JOIN usuario u ON t.id_tecnico = u.id_usuario " +
                     "WHERE u.nombre_completo LIKE ? AND u.activo = TRUE";
        List<Tecnico> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearTecnico(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar tecnico por nombre: " + e.getMessage(), e);
        }
    }

    public void actualizar(Tecnico tecnico) throws PersistenciaException {
        String sqlUsuario = "UPDATE usuario SET username = ?, nombre_completo = ?, " +
                            "telefono = ?, email = ?, activo = ? WHERE id_usuario = ?";
        String sqlTecnico = "UPDATE tecnico SET especializacion = ?, id_sucursal = ?, " +
                            "telefono = ?, activo = ? WHERE id_tecnico = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            con.setAutoCommit(false);

            PreparedStatement psUsuario = con.prepareStatement(sqlUsuario);
            psUsuario.setString(1, tecnico.getUsername());
            psUsuario.setString(2, tecnico.getNombreCompleto());
            psUsuario.setString(3, tecnico.getTelefono());
            psUsuario.setString(4, tecnico.getEmail());
            psUsuario.setBoolean(5, tecnico.isActivo());
            psUsuario.setInt(6, tecnico.getIdUsuario());
            psUsuario.executeUpdate();
            psUsuario.close();

            PreparedStatement psTecnico = con.prepareStatement(sqlTecnico);
            psTecnico.setString(1, tecnico.getEspecializacion());
            psTecnico.setInt(2, tecnico.getSucursal().getIdSucursal());
            psTecnico.setString(3, tecnico.getTelefono());
            psTecnico.setBoolean(5, tecnico.isActivo());
            psTecnico.setInt(6, tecnico.getIdUsuario());
            psTecnico.executeUpdate();
            psTecnico.close();

            con.commit();
            con.setAutoCommit(true);

        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar tecnico: " + e.getMessage(), e);
        }
    }

    // Convierte una fila del ResultSet en un objeto Tecnico
    private Tecnico mapearTecnico(ResultSet rs) throws SQLException, PersistenciaException {
        String nombreCompleto = rs.getString("nombre_completo");
        String[] partes = nombreCompleto.split(" ", 2);
        String nombre = partes[0];
        String apellido = partes.length > 1 ? partes[1] : "";

        Sucursal sucursal = sucursalDAO.buscarPorId(rs.getInt("id_sucursal"));

        Tecnico tecnico = new Tecnico(
            nombre,
            apellido,
            rs.getString("telefono"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("especializacion"),
            sucursal,
            rs.getString("email")
        );
        tecnico.setIdUsuario(rs.getInt("id_tecnico"));
        tecnico.setActivo(rs.getBoolean("activo"));
        return tecnico;
    }
}