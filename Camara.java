import java.math.BigDecimal;

public final class Camara extends Equipo {
    private final int resolucion;

    public Camara(String codigo, String marca, String modelo, BigDecimal tarifaDiaria, int resolucion) {
        super(codigo, marca, modelo, tarifaDiaria);
        if (resolucion <= 0) throw new IllegalArgumentException("La resolución debe ser mayor que cero.");
        this.resolucion = resolucion;
    }

    @Override
    protected BigDecimal calcularRecargo(int dias) {
        return resolucion > 1080 ? BigDecimal.valueOf(75) : BigDecimal.ZERO;
    }

    @Override
    public String getCategoria() { return "Cámara"; }

    @Override
    public String getCaracteristicas() { return "Resolución máxima: " + resolucion + " píxeles verticales"; }

    public int getResolucion() { return resolucion; }
}
