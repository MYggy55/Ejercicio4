import java.math.BigDecimal;
import java.util.ArrayList;

public final class EnEscena {
    private final ArrayList<Equipo> inventario;
    private BigDecimal ingresos;

    public EnEscena() {
        inventario = new ArrayList<>();
        ingresos = new BigDecimal("0.00");
    }

    public void registrarEquipo(Equipo equipo) {
        if (equipo == null) throw new IllegalArgumentException("El equipo no puede ser null.");
        for (Equipo registrado : inventario) {
            if (registrado.getCodigo().equalsIgnoreCase(equipo.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un equipo con ese código.");
            }
        }
        if (!equipo.isDisponible()) throw new IllegalArgumentException("El equipo nuevo debe estar disponible.");
        inventario.add(equipo);
    }

    public Equipo buscarEquipo(String codigo) {
        String buscado = Equipo.validarTexto(codigo, "Código");
        for (Equipo equipo : inventario) {
            if (equipo.getCodigo().equalsIgnoreCase(buscado)) return equipo;
        }
        throw new IllegalArgumentException("No existe un equipo con ese código.");
    }

    public ArrayList<Equipo> getInventario() { return new ArrayList<>(inventario); }

    public BigDecimal cotizar(String codigo, int dias) {
        return buscarEquipo(codigo).calcularCosto(dias);
    }

    public BigDecimal confirmarAlquiler(String codigo, int dias, boolean aceptado) {
        Equipo equipo = buscarEquipo(codigo);
        BigDecimal total = equipo.calcularCosto(dias);
        if (!equipo.isDisponible()) throw new IllegalStateException("El equipo ya está alquilado.");
        if (!aceptado) return new BigDecimal("0.00");
        BigDecimal nuevoIngreso = ingresos.add(total);
        equipo.alquilar();
        ingresos = nuevoIngreso;
        return total;
    }

    public void registrarDevolucion(String codigo) {
        buscarEquipo(codigo).devolver();
    }

    public int contarEquipos() { return inventario.size(); }

    public int contarPorCategoria(String categoria, boolean disponibles) {
        int cantidad = 0;
        for (Equipo equipo : inventario) {
            if (equipo.getCategoria().equals(categoria) && equipo.isDisponible() == disponibles) cantidad++;
        }
        return cantidad;
    }

    public ArrayList<String> getCategorias() {
        ArrayList<String> categorias = new ArrayList<>();
        for (Equipo equipo : inventario) {
            if (!categorias.contains(equipo.getCategoria())) categorias.add(equipo.getCategoria());
        }
        return categorias;
    }

    public BigDecimal getIngresos() { return ingresos; }
}
