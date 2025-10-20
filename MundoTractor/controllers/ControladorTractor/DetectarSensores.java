
// Importa las clases necesarias del API de Webots para controlar robots
import com.cyberbotics.webots.controller.*;

/**
 * Controlador TractorFase1 - Utilidad de diagnóstico
 * 
 * Este controlador tiene como objetivo identificar y listar todos los
 * dispositivos
 * disponibles en el modelo de tractor, especialmente los motores que controlan
 * el movimiento.
 * Es útil para debugging cuando no se conocen los nombres exactos de los
 * dispositivos.
 */
public class DetectarSensores {

    public DetectarSensores() {
    }

    public static void detectar() {

        // Crea una instancia del robot que representa al tractor en la simulación
        // Esta instancia permite interactuar con todos los dispositivos del robot
        Robot robot = new Robot();

        // Obtiene el time step básico de la simulación en milisegundos
        // El time step determina cada cuánto tiempo se ejecuta el bucle de control
        int timeStep = (int) Math.round(robot.getBasicTimeStep());

        // ===== SECCIÓN 1: LISTADO COMPLETO DE DISPOSITIVOS =====

        // Imprime un encabezado para identificar la sección en la consola
        System.out.println("=== DISPOSITIVOS DISPONIBLES ===");

        // Obtiene el número total de dispositivos asociados al robot
        int numberOfDevices = robot.getNumberOfDevices();

        // Itera a través de todos los dispositivos del robot
        for (int i = 0; i < numberOfDevices; i++) {
            // Obtiene el dispositivo en la posición i del array interno
            Device device = robot.getDeviceByIndex(i);

            // Imprime información del dispositivo:
            // - Índice numérico
            // - Nombre del dispositivo (identificador único)
            // - Tipo de clase (Motor, GPS, Camera, etc.)
            System.out.println("Dispositivo " + i + ": " + device.getName() +
                    " (" + device.getClass().getSimpleName() + ")");
        }
        System.out.println("=================================");

        // ===== SECCIÓN 2: BÚSQUEDA ESPECÍFICA DE MOTORES =====

        System.out.println("=== BUSCANDO MOTORES ===");

        // Vuelve a iterar por todos los dispositivos, pero esta vez
        // filtrando solo aquellos que son instancias de la clase Motor
        for (int i = 0; i < numberOfDevices; i++) {
            Device device = robot.getDeviceByIndex(i);

            // El operador 'instanceof' verifica si el dispositivo es un Motor
            if (device instanceof Motor) {
                // Convierte (castea) el Device genérico a un Motor específico
                Motor motor = (Motor) device;

                // Imprime el nombre del motor encontrado
                // Estos nombres son los que debemos usar en el controlador final
                System.out.println("Motor encontrado: " + motor.getName());
            }
        }
        System.out.println("=========================");

        // ===== SECCIÓN 3: MANTENER LA SIMULACIÓN ACTIVA =====

        // Ejecuta 10 ciclos de simulación para asegurar que:
        // - Los mensajes se muestren completamente en la consola
        // - La simulación no termine inmediatamente
        // - El usuario tenga tiempo de leer la información mostrada
        for (int i = 0; i < 10; i++) {
            robot.step(timeStep); // Avanza la simulación un time step
        }

        System.exit(0);
    }

}