package sistemaabarrotes;

public final class Distribuidor {
    private final String codigo;
    private String razonSocial;
    private String telefono;
    private String empresa;
    private String direccionEmpresa;
    private String ruc;

    public Distribuidor(String codigo, String razonSocial, String telefono,
                        String empresa, String direccionEmpresa, String ruc) {
        this.codigo = Validacion.texto(codigo, "El código de distribuidor");
        setRazonSocial(razonSocial);
        setTelefono(telefono);
        setEmpresa(empresa);
        setDireccionEmpresa(direccionEmpresa);
        setRuc(ruc);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return razonSocial; }
    public String getRazonSocial() { return razonSocial; }
    public String getTelefono() { return telefono; }
    public String getEmpresa() { return empresa; }
    public String getDireccionEmpresa() { return direccionEmpresa; }
    public String getRuc() { return ruc; }

    public void setRazonSocial(String valor) {
        razonSocial = Validacion.texto(valor, "La razón social");
    }

    public void setTelefono(String valor) {
        String telefonoValidado = Validacion.texto(valor, "El teléfono");
        if (!telefonoValidado.matches("\\d{7,15}")) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 15 números.");
        }
        telefono = telefonoValidado;
    }

    public void setEmpresa(String valor) { empresa = Validacion.texto(valor, "La empresa"); }
    public void setDireccionEmpresa(String valor) { direccionEmpresa = Validacion.texto(valor, "La dirección"); }

    public void setRuc(String valor) {
        String rucValidado = Validacion.texto(valor, "El RUC");
        if (!rucValidado.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe tener 11 números.");
        }
        ruc = rucValidado;
    }

    public void actualizarDatos(Distribuidor otro) {
        razonSocial = otro.razonSocial;
        telefono = otro.telefono;
        empresa = otro.empresa;
        direccionEmpresa = otro.direccionEmpresa;
        ruc = otro.ruc;
    }

    @Override
    public String toString() { return codigo + " - " + razonSocial; }
}
