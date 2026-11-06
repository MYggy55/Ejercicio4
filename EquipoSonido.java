import java.math.BigDecimal;

public final class EquipoSonido extends Equipo {
    private final BigDecimal potencia;

    public EquipoSonido(String codigo, String marca, String modelo, BigDecimal tarifaDiaria, BigDecimal potencia) {
        super(codigo, marca, modelo, tarifaDiaria);
        this.potencia = validarPositivo(potencia, "Potencia");
    }

    @Override
    protected BigDecimal calcularRecargo(int dias) {
        return BigDecimal.valueOf(100).multiply(potencia).multiply(BigDecimal.valueOf(dias));
    }

    @Override
    public String getCategoria() { return "Sonido"; }

    @Override
    public String getCaracteristicas() { return "Potencia nominal: " + potencia.toPlainString() + " kW"; }

    public BigDecimal getPotencia() { return potencia; }
}
