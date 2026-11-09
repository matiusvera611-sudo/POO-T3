package sistemaabarrotes;

public final class Cliente extends Persona {
    private String telefono;
    private String direccion;
    private String tipoCliente;
    private int puntos;

    public Cliente(String codigo, String nombre, String apellido, String tipodoc,
                   String nmrdoc, String correo, String telefono, String direccion,
                   String tipoCliente) {
        this(codigo, nombre, apellido, tipodoc, nmrdoc, correo, telefono, direccion, tipoCliente, 0);
    }

    public Cliente(String codigo, String nombre, String apellido, String tipodoc,
                   String nmrdoc, String correo, String telefono, String direccion,
                   String tipoCliente, int puntos) {
        super(codigo, nombre, apellido, tipodoc, nmrdoc, correo);
        setTelefono(telefono);
        setDireccion(direccion);
        setTipoCliente(tipoCliente);
        setPuntos(puntos);
    }

    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getTipoCliente() { return tipoCliente; }
    public int getPuntos() { return puntos; }

    public void setTelefono(String telefono) {
        String valor = Validacion.texto(telefono, "El teléfono");
        if (!valor.matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe contener 9 números.");
        }
        this.telefono = valor;
    }

    public void setDireccion(String direccion) {
        this.direccion = Validacion.texto(direccion, "La dirección");
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = Validacion.opcion(tipoCliente, "El tipo de cliente",
                "NORMAL", "FRECUENTE", "MAYORISTA");
    }

    public void setPuntos(int puntos) {
        this.puntos = Validacion.noNegativo(puntos, "Los puntos");
    }

    public void acumularPuntos(double monto) {
        Validacion.positivo(monto, "El monto de compra");
        long ganados = (long) (monto / 10);
        if (ganados > Integer.MAX_VALUE - puntos) {
            throw new IllegalArgumentException("Los puntos exceden el máximo permitido.");
        }
        puntos += (int) ganados;
    }

    public boolean esClienteFrecuente() {
        return puntos >= 50 || "FRECUENTE".equals(tipoCliente);
    }

    public void actualizarDatos(Cliente otro) {
        copiarDatos(otro);
        telefono = otro.telefono;
        direccion = otro.direccion;
        tipoCliente = otro.tipoCliente;
        puntos = otro.puntos;
    }

    @Override
    public String getTipo() { return "Cliente"; }
}
