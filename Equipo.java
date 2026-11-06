import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Equipo {
    private final String codigo;
    private final String marca;
    private final String modelo;
    private final BigDecimal tarifaDiaria;
    private boolean disponible;

    protected Equipo(String codigo, String marca, String modelo, BigDecimal tarifaDiaria) {
        this.codigo = validarTexto(codigo, "Código");
        this.marca = validarTexto(marca, "Marca");
        this.modelo = validarTexto(modelo, "Modelo");
        this.tarifaDiaria = validarPositivo(tarifaDiaria, "Tarifa diaria");
        this.disponible = true;
    }

    protected static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    protected static BigDecimal validarPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor que cero.");
        }
        return valor;
    }

    public final BigDecimal calcularCosto(int dias) {
        if (dias <= 0) throw new IllegalArgumentException("Los días deben ser mayores que cero.");
        return tarifaDiaria.multiply(BigDecimal.valueOf(dias)).add(calcularRecargo(dias))
            .setScale(2, RoundingMode.HALF_UP);
    }

    protected abstract BigDecimal calcularRecargo(int dias);
    public abstract String getCategoria();
    public abstract String getCaracteristicas();

    final void alquilar() {
        if (!disponible) throw new IllegalStateException("El equipo ya está alquilado.");
        disponible = false;
    }

    final void devolver() {
        if (disponible) throw new IllegalStateException("El equipo ya se encuentra disponible.");
        disponible = true;
    }

    public final String getCodigo() { return codigo; }
    public final String getMarca() { return marca; }
    public final String getModelo() { return modelo; }
    public final BigDecimal getTarifaDiaria() { return tarifaDiaria; }
    public final boolean isDisponible() { return disponible; }
}
