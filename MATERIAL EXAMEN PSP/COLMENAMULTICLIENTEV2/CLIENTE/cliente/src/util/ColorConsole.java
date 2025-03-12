package util;

public class ColorConsole {
    // Códigos ANSI para colores
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";

    // Métodos para imprimir con colores
    public static void printZangano(String mensaje) {
        System.out.println(PURPLE + mensaje + RESET); // Cambiado a morado (PURPLE)
    }

    public static void printLimpiadora(String mensaje) {
        System.out.println(GREEN + mensaje + RESET);
    }

    public static void printRecolectora(String mensaje) {
        System.out.println(BLUE + mensaje + RESET);
    }

    public static void printReina(String mensaje) {
        System.out.println(RED + mensaje + RESET); // Cambiado a rojo para la reina
    }

    public static void printNodriza(String mensaje) {
        System.out.println(YELLOW + mensaje + RESET);
    }

    public static void printInfo(String mensaje) {
        System.out.println(CYAN + mensaje + RESET);
    }
}