package sistemaabarrotes;

public final class Categoria {
    private final String IDcategoria;
    private String nombre;

    public Categoria(String IDcategoria, String nombre) {
        this.IDcategoria = Validacion.texto(IDcategoria, "El código de categoría");
        setNombre(nombre);
    }

    public String getIDcategoria() { return IDcategoria; }
    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = Validacion.texto(nombre, "El nombre de categoría");
    }

    @Override
    public String toString() { return IDcategoria + " - " + nombre; }
}
