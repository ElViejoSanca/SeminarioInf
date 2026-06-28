// OrdenServicioDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicioDAO {

    private TicketDAO ticketDAO = new TicketDAO();
    private TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private SucursalDAO sucursalDAO = new SucursalDAO();

    // Genera el siguiente numero de orden consultando el maximo existente
    private String generarSiguienteNumeroOrden(Connection con) throws SQLException {
        String sql = "SELECT numero_orden FROM orden_servicio ORDER BY id_orden DESC LIMIT 1";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        int siguiente = 1;
        if (rs.next()) {
            String ultimoNumero = rs.getString("numero_orden");
            String[] partes = ultimoNumero.split("-");
            siguiente = Integer.parseInt(partes[2]) + 1;
        }
        ps.close();
        return String.format("ORD-2026-%04d", siguiente);
    }

    public void insertar(OrdenServicio orden, int idUsuarioCreacion) throws PersistenciaException {
        String sql = "INSERT INTO orden_servicio (numero_orden, id_ticket, id_sucursal, " +
                     "id_usuario_creacion, tipo_servicio, prioridad, estado_actual, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();

            String numeroGenerado = generarSiguienteNumeroOrden(con);
            orden.setNumeroOrden(numeroGenerado);

            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, orden.getNumeroOrden());
            ps.setInt(2, orden.getTicket().getIdTicket());
            ps.setInt(3, orden.getSucursal().getIdSucursal());
            ps.setInt(4, idUsuarioCreacion);
            ps.setString(5, orden.getTipoServicio().name());
            ps.setString(6, orden.getPrioridad().name());
            ps.setString(7, orden.getEstadoActual().name());
            ps.setString(8, orden.getObservaciones());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                orden.setIdOrden(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar orden de servicio: " + e.getMessage(), e);
        }
    }

    public OrdenServicio buscarPorNumero(String numeroOrden) throws PersistenciaException {
        String sql = "SELECT * FROM orden_servicio WHERE numero_orden = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, numeroOrden);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearOrden(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar orden por numero: " + e.getMessage(), e);
        }
    }

    public List<OrdenServicio> listarPorEstado(EstadoOrden estado) throws PersistenciaException {
        String sql = "SELECT * FROM orden_servicio WHERE estado_actual = ? " +
                     "ORDER BY FIELD(prioridad, 'URGENTE','ALTA','MEDIA','BAJA')";
        List<OrdenServicio> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, estado.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearOrden(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar ordenes por estado: " + e.getMessage(), e);
        }
    }

    public List<OrdenServicio> listarTodas() throws PersistenciaException {
        String sql = "SELECT * FROM orden_servicio ORDER BY fecha_creacion DESC";
        List<OrdenServicio> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearOrden(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar ordenes: " + e.getMessage(), e);
        }
    }

    // Actualiza asignacion de tecnico y fecha coordinada
    public void actualizarAsignacion(OrdenServicio orden) throws PersistenciaException {
        String sql = "UPDATE orden_servicio SET id_tecnico = ?, estado_actual = ?, " +
                     "fecha_coordinada = ? WHERE id_orden = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, orden.getTecnico().getIdUsuario());
            ps.setString(2, orden.getEstadoActual().name());
            ps.setString(3, orden.getFechaCoordinada());
            ps.setInt(4, orden.getIdOrden());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar asignacion: " + e.getMessage(), e);
        }
    }

    // Actualiza solo el estado de la orden
    public void actualizarEstado(OrdenServicio orden) throws PersistenciaException {
        String sql = "UPDATE orden_servicio SET estado_actual = ? WHERE id_orden = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, orden.getEstadoActual().name());
            ps.setInt(2, orden.getIdOrden());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar estado de orden: " + e.getMessage(), e);
        }
    }

    // Actualiza resolucion y deja la orden pendiente de auditoria
    public void actualizarResolucion(OrdenServicio orden) throws PersistenciaException {
        String sql = "UPDATE orden_servicio SET resolucion_aplicada = ?, estado_actual = ? " +
                     "WHERE id_orden = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, orden.getResolucion());
            ps.setString(2, orden.getEstadoActual().name());
            ps.setInt(3, orden.getIdOrden());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar resolucion: " + e.getMessage(), e);
        }
    }

    // Registra el cierre formal de la orden tras auditoria
    public void cerrarOrden(OrdenServicio orden, int idUsuarioCierre) throws PersistenciaException {
        String sql = "UPDATE orden_servicio SET estado_actual = ?, observaciones = ?, " +
                     "id_usuario_cierre = ?, fecha_cierre = NOW() WHERE id_orden = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, orden.getEstadoActual().name());
            ps.setString(2, orden.getObservaciones());
            ps.setInt(3, idUsuarioCierre);
            ps.setInt(4, orden.getIdOrden());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al cerrar orden: " + e.getMessage(), e);
        }
    }

    // Convierte una fila del ResultSet en un objeto OrdenServicio
    private OrdenServicio mapearOrden(ResultSet rs) throws SQLException, PersistenciaException {
        Ticket ticket = ticketDAO.buscarPorId(rs.getInt("id_ticket"));
        Sucursal sucursal = sucursalDAO.buscarPorId(rs.getInt("id_sucursal"));

        OrdenServicio orden = new OrdenServicio(
            ticket,
            TipoServicio.valueOf(rs.getString("tipo_servicio")),
            Prioridad.valueOf(rs.getString("prioridad")),
            sucursal,
            rs.getString("observaciones")
        );
        orden.setIdOrden(rs.getInt("id_orden"));
        orden.setNumeroOrden(rs.getString("numero_orden"));
        orden.setEstadoActual(EstadoOrden.valueOf(rs.getString("estado_actual")));
        orden.setFechaCoordinada(rs.getString("fecha_coordinada"));
        orden.setResolucion(rs.getString("resolucion_aplicada"));

        int idTecnico = rs.getInt("id_tecnico");
        if (!rs.wasNull()) {
            Tecnico tecnico = tecnicoDAO.buscarPorId(idTecnico);
            orden.setTecnico(tecnico);
        }

        return orden;
    }
}