// UsuarioSistema.java
public abstract class UsuarioSistema extends PersonaSistema {

    private int idUsuario;
    private String username;
    private String password;
    private String rol;
    private String email;
    private boolean activo;

    // Constructor
    public UsuarioSistema(String nombre, String apellido, String telefono,
                          String username, String password, String rol, String email) {
        super(nombre, apellido, telefono);
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }

        this.idUsuario = 0; // Se asignará desde la base de datos
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.email = email;
        this.activo = true;
    }

    // Verifica credenciales de acceso
    public boolean autenticar(String usernameIngresado, String passwordIngresada) {
        if (usernameIngresado == null || passwordIngresada == null) {
            throw new IllegalArgumentException("Las credenciales no pueden ser nulas");
        }
        return this.username.equals(usernameIngresado) &&
               this.password.equals(passwordIngresada) &&
               this.activo;
    }

    // Getters y Setters
    @Override
    public int getId() { return idUsuario; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        this.username = username;
    }

    // Password no tiene getter por seguridad, solo setter
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail (String email){
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("El email no es valido");
        }
        this.email = email;
    }

    protected String getPassword() { return password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}