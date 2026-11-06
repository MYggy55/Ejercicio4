import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EnEscena empresa = crearDemostracion();
        try (Scanner entrada = new Scanner(System.in)) {
            System.out.println("ENESCENA | ALQUILER DE EQUIPOS");
            System.out.println("Seis equipos iniciales disponibles. Ingresos: Q0.00.");
            System.out.println("Escribe los decimales con punto.");
            boolean continuar = true;
            while (continuar) {
                mostrarMenu();
                int opcion = leerEntero(entrada, "Opción: ");
                try {
                    switch (opcion) {
                        case 1 -> registrarEquipo(entrada, empresa);
                        case 2 -> mostrarInventario(empresa);
                        case 3 -> cotizar(entrada, empresa);
                        case 4 -> alquilar(entrada, empresa);
                        case 5 -> {
                            empresa.registrarDevolucion(leerTexto(entrada, "Código del equipo: "));
                            System.out.println("Devolución registrada. No se modifica el ingreso acumulado.");
                        }
                        case 6 -> mostrarReporte(empresa);
                        case 7 -> continuar = false;
                        default -> System.out.println("Selecciona una opción entre 1 y 7.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Operación rechazada: " + e.getMessage());
                }
            }
            System.out.println("Programa finalizado.");
        } catch (NoSuchElementException e) {
            System.out.println("Entrada finalizada. Programa cerrado.");
        }
    }

    private static EnEscena crearDemostracion() {
        EnEscena empresa = new EnEscena();
        empresa.registrarEquipo(new Proyector("P001", "Epson", "X49", new BigDecimal("100"), 3600, false));
        empresa.registrarEquipo(new Proyector("P002", "BenQ", "EW600", new BigDecimal("100"), 3600, true));
        empresa.registrarEquipo(new Camara("C001", "Canon", "Vixia", new BigDecimal("150"), 1080));
        empresa.registrarEquipo(new Camara("C002", "Sony", "AX43", new BigDecimal("150"), 2160));
        empresa.registrarEquipo(new EquipoSonido("S001", "JBL", "EON", new BigDecimal("200"), new BigDecimal("1.5")));
        empresa.registrarEquipo(new EquipoSonido("S002", "Yamaha", "Stagepas", new BigDecimal("180"), new BigDecimal("0.8")));
        return empresa;
    }

    private static void mostrarMenu() {
        System.out.println("\n1. Registrar equipo\n2. Consultar inventario\n3. Cotizar alquiler"
            + "\n4. Confirmar alquiler\n5. Registrar devolución\n6. Reporte general\n7. Salir");
    }

    private static void registrarEquipo(Scanner entrada, EnEscena empresa) {
        int categoria = leerEntero(entrada, "Categoría: 1 = Proyector, 2 = Cámara, 3 = Sonido: ");
        if (categoria < 1 || categoria > 3) throw new IllegalArgumentException("Categoría inválida.");
        String codigo = leerTexto(entrada, "Código: ");
        String marca = leerTexto(entrada, "Marca: ");
        String modelo = leerTexto(entrada, "Modelo: ");
        BigDecimal tarifa = leerDecimal(entrada, "Tarifa diaria: Q");
        Equipo equipo;
        if (categoria == 1) {
            int luminosidad = leerEntero(entrada, "Luminosidad en lúmenes: ");
            boolean inalambrico = leerSiNo(entrada, "¿Conectividad inalámbrica? (s/n): ");
            equipo = new Proyector(codigo, marca, modelo, tarifa, luminosidad, inalambrico);
        } else if (categoria == 2) {
            int resolucion = leerEntero(entrada, "Resolución máxima en píxeles verticales: ");
            equipo = new Camara(codigo, marca, modelo, tarifa, resolucion);
        } else {
            BigDecimal potencia = leerDecimal(entrada, "Potencia nominal en kW: ");
            equipo = new EquipoSonido(codigo, marca, modelo, tarifa, potencia);
        }
        empresa.registrarEquipo(equipo);
        System.out.println("Equipo registrado y disponible.");
    }

    private static void mostrarInventario(EnEscena empresa) {
        if (empresa.contarEquipos() == 0) System.out.println("No hay equipos registrados.");
        for (Equipo equipo : empresa.getInventario()) mostrarEquipo(equipo);
    }

    private static void mostrarEquipo(Equipo equipo) {
        System.out.println(equipo.getCodigo() + " | " + equipo.getCategoria() + " | " + equipo.getMarca()
            + " | " + equipo.getModelo() + " | Tarifa diaria: " + dinero(equipo.getTarifaDiaria())
            + " | " + (equipo.isDisponible() ? "Disponible" : "Alquilado")
            + " | " + equipo.getCaracteristicas());
    }

    private static void cotizar(Scanner entrada, EnEscena empresa) {
        String codigo = leerTexto(entrada, "Código: ");
        Equipo equipo = empresa.buscarEquipo(codigo);
        int dias = leerEntero(entrada, "Días de alquiler: ");
        BigDecimal total = empresa.cotizar(codigo, dias);
        mostrarEquipo(equipo);
        System.out.println("Cotización para " + dias + " días: " + dinero(total));
        System.out.println("Cotizar no registra un alquiler ni modifica los ingresos.");
    }

    private static void alquilar(Scanner entrada, EnEscena empresa) {
        String codigo = leerTexto(entrada, "Código: ");
        Equipo equipo = empresa.buscarEquipo(codigo);
        if (!equipo.isDisponible()) throw new IllegalStateException("El equipo ya está alquilado.");
        int dias = leerEntero(entrada, "Días de alquiler: ");
        BigDecimal total = empresa.cotizar(codigo, dias);
        mostrarEquipo(equipo);
        System.out.println("Total a cobrar por " + dias + " días: " + dinero(total));
        boolean aceptado = leerSiNo(entrada, "¿Confirmar alquiler? (s/n): ");
        BigDecimal cobrado = empresa.confirmarAlquiler(codigo, dias, aceptado);
        System.out.println(aceptado ? "Alquiler confirmado. Cobrado: " + dinero(cobrado)
            : "Alquiler cancelado. El equipo y los ingresos conservan su estado.");
    }

    private static void mostrarReporte(EnEscena empresa) {
        System.out.println("\nREPORTE GENERAL");
        System.out.println("Equipos registrados: " + empresa.contarEquipos());
        int disponibles = 0;
        int alquilados = 0;
        for (String categoria : empresa.getCategorias()) {
            int libres = empresa.contarPorCategoria(categoria, true);
            int ocupados = empresa.contarPorCategoria(categoria, false);
            disponibles += libres;
            alquilados += ocupados;
            System.out.println(categoria + " | Registrados: " + (libres + ocupados)
                + " | Disponibles: " + libres + " | Alquilados: " + ocupados);
        }
        System.out.println("Total disponibles: " + disponibles + " | Total alquilados: " + alquilados);
        System.out.println("Ingresos acumulados: " + dinero(empresa.getIngresos()));
    }

    private static String leerTexto(Scanner entrada, String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private static int leerEntero(Scanner entrada, String mensaje) {
        Integer valor = null;
        while (valor == null) {
            String texto = leerTexto(entrada, mensaje);
            try {
                valor = Integer.valueOf(texto);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un entero dentro del rango de int.");
            }
        }
        return valor;
    }

    private static BigDecimal leerDecimal(Scanner entrada, String mensaje) {
        BigDecimal valor = null;
        while (valor == null) {
            String texto = leerTexto(entrada, mensaje);
            try {
                valor = new BigDecimal(texto);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un número decimal con punto.");
            }
        }
        return valor;
    }

    private static boolean leerSiNo(Scanner entrada, String mensaje) {
        String valor = leerTexto(entrada, mensaje);
        while (!valor.equalsIgnoreCase("s") && !valor.equalsIgnoreCase("n")) {
            System.out.println("Responde s o n.");
            valor = leerTexto(entrada, mensaje);
        }
        return valor.equalsIgnoreCase("s");
    }

    private static String dinero(BigDecimal monto) {
        return "Q" + monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
