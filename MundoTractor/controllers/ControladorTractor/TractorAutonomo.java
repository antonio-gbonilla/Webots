import com.cyberbotics.webots.controller.vehicle.*;

/**
 * Clase que representa un tractor autónomo en Webots.
 * Extiende de Car y contiene métodos para controlar su movimiento.
 */
public class TractorAutonomo extends Car {

    private int timeStep;

    /**
     * Constructor de TractorAutonomo.
     * Inicializa la instancia y obtiene el timeStep de la simulación.
     */
    public TractorAutonomo() {
        super();
        timeStep = (int) getBasicTimeStep(); // Se corta la parte decima 1.9 = 1
    }

    /**
     * Hace que el tractor avance recto durante un tiempo determinado.
     * La velocidad y la dirección se pueden ajustar dentro del método.
     */
    public void avanzar() {
        double velocidad = 5.0;
        setCruisingSpeed(velocidad); // Avanzas velocidad Crucero
        setSteeringAngle(0.0); // Angulo en el que giran las ruedas
        esperar(2000);
    }

    /**
     * Hace que el tractor gire aproximadamente 90 grados hacia la derecha.
     * Mantiene la velocidad y la dirección durante un tiempo aproximado.
     * Luego endereza las ruedas.
     */
    public void girar(double angle) {
        double angleRad = Math.toRadians(angle);
        double velocidad = 5.0;
        setCruisingSpeed(velocidad);
        setSteeringAngle(angleRad);

        esperar(1500);

        setSteeringAngle(0.0);
    }

    /**
     * Detiene el tractor completamente usando freno realista.
     */
    public void frenar() {
        // Aplica freno al máximo
        setBrakeIntensity(1.0);
        setCruisingSpeed(0.0);
        setSteeringAngle(0.0); // ruedas rectas

        // Mantiene la simulación unos pasos para asegurar que se detenga
        esperar(timeStep);

        // Desactiva el freno para permitir futuros movimientos
        setBrakeIntensity(0.0);
    }

    /**
     * Hace que el tractor avance hacia atrás usando la marcha atrás real.
     */
    public void marchaAtras() {
        setSteeringAngle(0.0);
        setCruisingSpeed(-5.0); // velocidad negativa = marcha atrás
        esperar(2000);
        setCruisingSpeed(0.0);
    }

    /**
     * Mantiene la simulación activa durante el tiempo especificado.
     * 
     * @param durationMs Duración en milisegundos
     */
    private void esperar(int durationMs) {
        int steps = (int) (durationMs / timeStep); // Calcula cuantos pasos tiene que dar a partir del tiempo
        for (int i = 0; i < steps; i++) {
            if (step(timeStep) == -1)
                break; // sale del bucle for si la simulación termina
        }
    }

}
