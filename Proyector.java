import java.math.BigDecimal;

public final class Proyector extends Equipo {
    private final int luminosidad;
    private final boolean inalambrico;

    public Proyector(String codigo, String marca, String modelo, BigDecimal tarifaDiaria, int luminosidad, boolean inalambrico) {
        super(codigo, marca, modelo, tarifaDiaria);
        if (luminosidad <= 0) throw new IllegalArgumentException("La luminosidad debe ser mayor que cero.");
        this.luminosidad = luminosidad;
        this.inalambrico = inalambrico;
    }

    @Override
    protected BigDecimal calcularRecargo(int dias) {
        return inalambrico ? BigDecimal.valueOf(50).multiply(BigDecimal.valueOf(dias)) : BigDecimal.ZERO;
    }

    @Override
    public String getCategoria() { return "Proyector"; }

    @Override
    public String getCaracteristicas() {
        return luminosidad + " lúmenes | Inalámbrico: " + (inalambrico ? "sí" : "no");
    }

    public int getLuminosidad() { return luminosidad; }
    public boolean isInalambrico() { return inalambrico; }
}
