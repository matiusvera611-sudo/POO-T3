package sistemaabarrotes;

/** Clase abstracta: reúne los datos comunes de Cliente y Usuario. */
public abstract class Persona {
    private final String codigo;
    private String nombre;
    private String apellido = "";
    private String tipodoc = "";
    private String nmrdoc = "";
    private String correo = "";

    public Persona(String codigo, String nombre) {
        this.codigo = Validacion.texto(codigo, "El código");
        setNombre(nombre);
    }

    public Persona(String codigo, String nombre, String apellido,
                   String tipodoc, String nmrdoc, String correo) {
        this(codigo, nombre);
        setApellido(apellido);
        setTipodoc(tipodoc);
        setNmrdoc(nmrdoc);
        setCorreo(correo);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTipodoc() { return tipodoc; }
    public String getNmrdoc() { return nmrdoc; }
    public String getCorreo() { return correo; }

    public final void setNombre(String nombre) {
        this.nombre = Validacion.nombres(nombre, "El nombre");
    }

    public final void setApellido(String apellido) {
        this.apellido = Validacion.nombres(apellido, "El apellido");
    }

    public final void setTipodoc(String tipodoc) {
        this.tipodoc = Validacion.opcion(tipodoc, "El tipo de documento", "DNI", "RUC", "CE");
    }

    public final void setNmrdoc(String nmrdoc) {
        String documento = Validacion.texto(nmrdoc, "El número de documento");
        boolean valido = ("DNI".equals(tipodoc) && documento.matches("\\d{8}"))
                || ("RUC".equals(tipodoc) && documento.matches("\\d{11}"))
                || ("CE".equals(tipodoc) && documento.matches("[a-zA-Z0-9]{9,12}"));
        if (!valido) {
            throw new IllegalArgumentException("Documento inválido: DNI de 8 dígitos, RUC de 11 o CE de 9 a 12 caracteres.");
        }
        this.nmrdoc = documento;
    }

    public final void setCorreo(String correo) {
        String valor = Validacion.texto(correo, "El correo");
        if (!valor.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        this.correo = valor;
    }

    protected void copiarDatos(Persona otra) {
        nombre = otra.nombre;
        apellido = otra.apellido;
        tipodoc = otra.tipodoc;
        nmrdoc = otra.nmrdoc;
        correo = otra.correo;
    }

    public boolean validarIdentificacion(String tipo, String documento) {
        return tipodoc.equalsIgnoreCase(tipo) && nmrdoc.equals(documento);
    }

    public String obtenerDatos() {
        return getTipo() + ": " + codigo + " - " + nombre + " " + apellido;
    }

    public abstract String getTipo();

    @Override
    public String toString() {
        return codigo + " - " + nombre + (apellido.isEmpty() ? "" : " " + apellido);
    }
}
