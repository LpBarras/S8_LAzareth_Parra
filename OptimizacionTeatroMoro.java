/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

//LAzareth Parra

package com.mycompany.optimizacionteatromoro;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class OptimizacionTeatroMoro {

    // Cantidad total de asientos en el teatro
    private final int totalAsientos = 50;
    private final int precioBase = 5200;

    // Array para indicar si el asiento está disponible
    private boolean[] asientosDisponibles = new boolean[totalAsientos];
    // Estado actual de cada asiento: Disponible, Reservado, Vendido
    private String[] estadoAsientos = new String[totalAsientos];
    // Tipo de entrada vendida o reservada: Normal, Estudiante, Tercera Edad
    private String[] tipoEntradas = new String[totalAsientos];
    // Precio final pagado por cada asiento
    private int[] preciosPagados = new int[totalAsientos];
    // Indica si se ha impreso la boleta para un asiento vendido
    private boolean[] boletaImpresa = new boolean[totalAsientos];
    // Nombre del cliente que reservó o compró el asiento
    private String[] clientes = new String[totalAsientos];

  
    private int totalEntradasVendidas = 0;
    private int totalIngresos = 0;

    // Lista de todas las reservas activas realizadas
    private List<Reserva> reservas = new ArrayList<>();
    // Mapa que relaciona nombres de clientes con sus ids, para asociar en caso de que el cliente relice mas de una compra
    private Map<String, String> mapaClientes = new HashMap<>();


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OptimizacionTeatroMoro teatro = new OptimizacionTeatroMoro();
        teatro.menu(scanner);
    }

    public OptimizacionTeatroMoro() {
        for (int i = 0; i < totalAsientos; i++) {
            asientosDisponibles[i] = true;
            estadoAsientos[i] = "Disponible";
            tipoEntradas[i] = "";
            preciosPagados[i] = 0;
            boletaImpresa[i] = false;
            clientes[i] = "";
        }
    }

    // Muestra el menú principal con opciones disponibles
    private void menu(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- Teatro Moro ---");
            System.out.println("1. Reservar Entrada");
            System.out.println("2. Comprar Entradas");
            System.out.println("3. Modificar Venta");
            System.out.println("4. Ver disponiblidad de Asientos");
            System.out.println("5. Salir");

            opcion = leerNumeroConValidacion(scanner, "Seleccione una opción: ", 1, 5); //Verifica entrada valida

            switch (opcion) {
                case 1 -> reservarEntrada(scanner);
                case 2 -> comprarEntradas(scanner);
                case 3 -> modificarVenta(scanner);
                case 4 -> mostrarAsientos();
                case 5 -> mostrarResumen();
            }
        } while (opcion != 5); //mantiene el menu abierto
    }

    // Permite al usuario reservar un asiento y su tipo de entrada
    public void reservarEntrada(Scanner scanner) {
        mostrarAsientos();
        int asiento = leerNumeroConValidacion(scanner,"Seleccione asiento: ", 1, totalAsientos) - 1;

        if (!asientosDisponibles[asiento]) {
            System.out.println("Asiento no disponible.");
            return;
        }

        System.out.print("Nombre del cliente: ");
        String cliente = scanner.nextLine().trim(); //evita duplicados por espacios extras

        String tipo = leerTipoEntrada(scanner);

        asientosDisponibles[asiento] = false;
        estadoAsientos[asiento] = "Reservado";
        tipoEntradas[asiento] = tipo;
        clientes[asiento] = cliente;

        String idReserva = String.format("R%03d", reservas.size() + 1); //id de 3 digitos con R y el numero de reserva
        reservas.add(new Reserva(asiento, tipo, idReserva));

        mapaClientes.putIfAbsent(cliente, "C" + String.format("%03d", mapaClientes.size() + 1)); //añade un id al cliente solo si este ya no fue ingresado anteriormente

        System.out.println("Reserva realizada con éxito. Su ID de reserva es de: " + idReserva);
    }

    // compra de todos los asientos reservados
public void comprarEntradas(Scanner scanner) {
    boolean tieneReservas = false;
    int precio; //a pagar

    for (Reserva r : reservas) {
        if (estadoAsientos[r.asiento].equals("Reservado")) { 
            tieneReservas = true;
            // calcular el precio solo si el asiento está reservado
            if (r.tipoEntrada.equals("Estudiante")) {
                precio = (int) (precioBase * 0.9); //10% de descuento
            } else if (r.tipoEntrada.equals("Tercera Edad")) {
                precio = (int) (precioBase * 0.85); //15% de descuento
            } else {
                precio = precioBase;
            }

            // Procesar la venta
            preciosPagados[r.asiento] = precio; //almacena los precios con su descuento
            totalIngresos += precio;
            totalEntradasVendidas++;
            estadoAsientos[r.asiento] = "Vendido";  // Marcar asiento como vendido
        }
    }

    // Si no hay reservas, notifica l usuario
    if (!tieneReservas) {
        System.out.println("No hay asientos reservados.");
        return;
    }

    // Limpiar las reservas ya compradas
    reservas.clear();

    // Preguntar si desea imprimir boletas
    System.out.println("\n¿Desea imprimir boleta? (1 - Sí / 2 - No): ");
    int boleta = leerNumeroConValidacion(scanner, "", 1, 2);
    if (boleta == 1) {
        imprimirBoletas();
    }
}

    // Permite cancelar una venta ya realizada
    public void modificarVenta(Scanner scanner) {
        int asiento = leerNumeroConValidacion(scanner, "Ingrese el asiento comprado que desea devolver (1-" + totalAsientos + "): ", 1, totalAsientos) - 1;

        if (!estadoAsientos[asiento].equals("Vendido")) {
            System.out.println("Este asiento no ha sido vendido aún.");
            return;
        }

        estadoAsientos[asiento] = "Disponible";
        asientosDisponibles[asiento] = true;
        totalIngresos -= preciosPagados[asiento];
        totalEntradasVendidas--;
        tipoEntradas[asiento] = "";
        preciosPagados[asiento] = 0;
        boletaImpresa[asiento] = false;
        clientes[asiento] = "";

        System.out.println("Venta cancelada con éxito.");
    }

    // Imprime las boletas de todos los asientos vendidos y no impresos
    public void imprimirBoletas() {
        System.out.println("\n--- BOLETAS ---");
        for (int i = 0; i < totalAsientos; i++) {
            if (estadoAsientos[i].equals("Vendido") && !boletaImpresa[i]) {
                System.out.println("Asiento: " + (i + 1));
                System.out.println("Cliente: " + clientes[i]);
                System.out.println("ID Cliente: " + mapaClientes.get(clientes[i]));
                System.out.println("Tipo Entrada: " + tipoEntradas[i]);
                System.out.println("Precio: $" + preciosPagados[i]);
                System.out.println("----------------------");
                boletaImpresa[i] = true;
            }
        }
    }

    // Muestra un resumen visual del estado de los asientos
    private void mostrarAsientos() {
        System.out.println("\nEstado de asientos: D-disponible,R-reservado y V-vendido");
         System.out.println("-------------Pantalla---------------");
        for (int i = 0; i < totalAsientos; i++) {
            if (i % 10 == 0) System.out.println();
         
            if (estadoAsientos[i].equals("Disponible")) {
                System.out.print((i + 1) + "D"+" ");
            } else if (estadoAsientos[i].equals("Reservado")) {
                System.out.print("R ");
            } else if (estadoAsientos[i].equals("Vendido")) {
                System.out.print("V ");
            }
        }
        System.out.println();
    }

    // Muestra el resumen del estado de ventas y ocupación del "dia"
    private void mostrarResumen() {
        System.out.println("\n--- RESUMEN ---");
        System.out.println("Entradas vendidas: " + totalEntradasVendidas);
        System.out.println("Ingresos totales: $" + totalIngresos);
        System.out.printf("Porcentaje de ocupación: %.2f%%\n", (totalEntradasVendidas / (double) totalAsientos) * 100);
    }

    // Valida que la entrada sea numérica y esté dentro del rango permitido
    private int leerNumeroConValidacion(Scanner scanner, String mensaje, int min, int max) {
        int numero = 0;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensaje);
            try {
                numero = Integer.parseInt(scanner.nextLine());
                if (numero >= min && numero <= max) {
                    valido = true;
                } else {
                    System.out.println("Debe ingresar un numero dentro lo especificado. Intnte nuevamente");
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida, debe ingresar un numero");
            }
        }
        return numero;
    }

    // Muestra opciones de tipo de entrada y retorna la selección
    private String leerTipoEntrada(Scanner scanner) {
        System.out.println("Tipo de entrada:");
        System.out.println("1. Normal");
        System.out.println("2. Estudiante (10% desc.)");
        System.out.println("3. Tercera Edad (15% desc.)");
        int opcion = leerNumeroConValidacion(scanner, "Seleccione una opción: ", 1, 3);

        return switch (opcion) {
            case 2 -> "Estudiante";
            case 3 -> "Tercera Edad";
            default -> "Normal";
        };
    }

    // Clase interna que representa una reserva realizada por un cliente
    private class Reserva {
        int asiento;             // Número de asiento reservado
        String tipoEntrada;      // Tipo de entrada reservada
        String idReserva;        // ID único de la reserva

        public Reserva(int asiento, String tipoEntrada, String idReserva) {
            this.asiento = asiento;
            this.tipoEntrada = tipoEntrada;
            this.idReserva = idReserva;
        }
    }
}

