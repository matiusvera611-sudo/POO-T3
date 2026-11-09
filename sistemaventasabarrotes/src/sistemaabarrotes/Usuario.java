package sistemaabarrotes;

public final class Usuario extends Persona {
    private String rol;
    private String password;
    private String nombreUsuario;
    private boolean estado = true;

    public Usuario(String codigo, String nombre, String password, String rol) {
        super(codigo, nombre);
        setPassword(password);
        setRol(rol);
        setNombreUsuario(codigo);
    }

    public Usuario(String codigo, String nombre, String apellido, String tipodoc,
                   String nmrdoc, String correo, String rol, String password,
                   String nombreUsuario, boolean estado) {
        this(codigo, nombre, password, rol);
        if (!apellido.isEmpty()) { setApellido(apellido); }
        if (!tipodoc.isEmpty()) {
            setTipodoc(tipodoc);
            setNmrdoc(nmrdoc);
        }
        if (!correo.isEmpty()) { setCorreo(correo); }
        setNombreUsuario(nombreUsuario);
        setEstado(estado);
    }

    public String getRol() { return rol; }
    public String getPassword() { return password; }
    public String getNombreUsuario() { return nombreUsuario; }
    public boolean isEstado() { return estado; }

    public void setRol(String rol) {
        this.rol = Validacion.opcion(rol, "El rol", "Titular Gerente", "Técnico", "Vendedor");
    }

    public void setPassword(String password) {
        this.password = Validacion.texto(password, "La contraseña");
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = Validacion.texto(nombreUsuario, "El nombre de usuario");
    }

    public void setEstado(boolean estado) { this.estado = estado; }

    public boolean validarAcceso(String usuario, String clave) {
        return estado && usuario != null && clave != null
                && (getCodigo().equalsIgnoreCase(usuario.trim())
                    || nombreUsuario.equalsIgnoreCase(usuario.trim()))
                && password.equals(clave);
    }

    public void actualizarDatos(Usuario otro) {
        setNombre(otro.getNombre());
        password = otro.password;
        rol = otro.rol;
    }

    @Override
    public String getTipo() { return "Usuario"; }
}
