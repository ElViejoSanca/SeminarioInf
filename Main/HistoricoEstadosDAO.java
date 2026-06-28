// HistoricoEstadosDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoricoEstadosDAO {

    public void insertar(HistoricoEstado historico) throws PersistenciaException {
        String sql = "INSERT INTO historico_estados (id_orden, estado_anterior, estado_nuevo, " +
                     "id_usuario, observaciones) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, historico.getIdOrden());
            ps.setString(2, historico.getEstadoAnterior());
            ps.setString(3, historico.getEstadoNuevo());
            ps.setInt(4, historico.getIdUsuario());
            ps.setString(5, historico.getObservaciones());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                historico.setIdHistorico(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar historico de estado: " + e.getMessage(), e);
        }
    }

    public List<HistoricoEstado> listarPorOrden(int idOrden) throws PersistenciaException {
        String sql = "SELECT h.*, u.nombre_completo FROM historico_estados h " +
                     "INNER JOIN usuario u ON h.id_usuario = u.id_usuario " +
                     "WHERE h.id_orden = ? ORDER BY h.fecha_cambio ASC";
        List<HistoricoEstado> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idOrden);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearHistorico(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar historico de orden: " + e.getMessage(), e);
        }
    }

    // Convierte una fila del ResultSet en un objeto HistoricoEstado
    private HistoricoEstado mapearHistorico(ResultSet rs) throws SQLException {
        HistoricoEstado h = new HistoricoEstado(
            rs.getInt("id_orden"),
            rs.getString("estado_anterior"),
            rs.getString("estado_nuevo"),
            rs.getInt("id_usuario"),
            rs.getString("observaciones")
        );
        h.setIdHistorico(rs.getInt("id_historico"));
        h.setNombreUsuario(rs.getString("nombre_completo"));
        h.setFechaCambio(rs.getString("fecha_cambio"));
        return h;
    }
}
