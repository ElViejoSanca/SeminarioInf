// ConexionBD.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/sigiost?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "Brisa_Doki25";

    private static ConexionBD instancia;
    private Connection conexion;

    // Constructor privado: nadie fuera de esta clase puede instanciarla
    private ConexionBD() throws SQLException {
        this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    // Punto unico de acceso a la instancia (patron Singleton)
    public static ConexionBD getInstancia() throws SQLException {
        if (instancia == null || instancia.conexion.isClosed()) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public static void getInstancia_sinExcepcion() {
    try {
        if (instancia != null) {
            instancia.cerrarConexion();
        }
    } catch (Exception e) {
        System.out.println("Error al cerrar conexion: " + e.getMessage());
    }
}

    public Connection getConexion() {
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }
}