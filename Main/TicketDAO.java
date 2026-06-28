// TicketDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private ClienteDAO clienteDAO = new ClienteDAO();

    public void insertar(Ticket ticket, int idUsuarioRegistro) throws PersistenciaException {
        String sql = "INSERT INTO ticket (id_cliente, id_usuario_registro, descripcion_problema, " +
                     "prioridad, estado, area_destino) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ticket.getCliente().getIdCliente());
            ps.setInt(2, idUsuarioRegistro);
            ps.setString(3, ticket.getDescripcion());
            ps.setString(4, ticket.getPrioridad().name());
            ps.setString(5, ticket.getEstado().name());
            ps.setString(6, ticket.getAreaDestino());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ticket.setIdTicket(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar ticket: " + e.getMessage(), e);
        }
    }

    public Ticket buscarPorId(int id) throws PersistenciaException {
        String sql = "SELECT * FROM ticket WHERE id_ticket = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearTicket(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar ticket por id: " + e.getMessage(), e);
        }
    }

    public List<Ticket> listarPorEstado(EstadoTicket estado) throws PersistenciaException {
        String sql = "SELECT * FROM ticket WHERE estado = ? ORDER BY fecha_registro DESC";
        List<Ticket> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, estado.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearTicket(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar tickets por estado: " + e.getMessage(), e);
        }
    }

    public List<Ticket> listarPorCliente(int idCliente) throws PersistenciaException {
        String sql = "SELECT * FROM ticket WHERE id_cliente = ? ORDER BY fecha_registro DESC";
        List<Ticket> resultado = new ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultado.add(mapearTicket(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar tickets por cliente: " + e.getMessage(), e);
        }
    }

    // Actualiza solo el estado del ticket (usado al convertir a orden)
    public void actualizarEstado(Ticket ticket) throws PersistenciaException {
        String sql = "UPDATE ticket SET estado = ? WHERE id_ticket = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ticket.getEstado().name());
            ps.setInt(2, ticket.getIdTicket());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar estado del ticket: " + e.getMessage(), e);
        }
    }

    // Convierte una fila del ResultSet en un objeto Ticket
    private Ticket mapearTicket(ResultSet rs) throws SQLException, PersistenciaException {
        Cliente cliente = clienteDAO.buscarPorId(rs.getInt("id_cliente"));

        Ticket ticket = new Ticket(
            cliente,
            rs.getString("descripcion_problema"),
            Prioridad.valueOf(rs.getString("prioridad")),
            rs.getString("area_destino")
        );
        ticket.setIdTicket(rs.getInt("id_ticket"));
        ticket.setEstado(EstadoTicket.valueOf(rs.getString("estado")));
        return ticket;
    }
}