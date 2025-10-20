import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.InertialUnit;
import com.cyberbotics.webots.controller.vehicle.Car;

/**
 * Clase que representa un tractor autónomo en Webots.
 * Extiende de Car y contiene métodos para controlar su movimiento.
 */
public class TractorAutonomo extends Car {

    // Tiempo que tarda en ejecutar un movimiento en webots
    private int timeStep;

    // Valor maximo para que los distance sensor indiquen que hay un obstaculo
    private final double OBSTACLE_DISTANCE = 950.0;

    // Maxima velocidad de los motores de las ruedas
    private double MAX_VELOCITY = 10.0;

    // Sensores de distancia del vehículo. Tiene 2 en la parte delantera.
    private DistanceSensor[] ds;

    // Variable para sacar la orientacion del tractor
    private InertialUnit orientacion;

    /**
     * Proporciona las cordenadas del vehiculo
     */
    private GPS gps;

    /**
     * Constructor de TractorAutonomo.
     * Inicializa la instancia y obtiene el timeStep de la simulación.
     */
    public TractorAutonomo() {
        super();
        timeStep = (int) getBasicTimeStep(); // Se corta la parte decima 1.9 = 1
        System.out.println(timeStep);
        initSesores();
    }

    private void initSesores() {
        ds = new DistanceSensor[2];
        String[] nombreDS = { "ds_left", "ds_right" };
        for (int i = 0; i < ds.length; i++) {
            ds[i] = getDistanceSensor(nombreDS[i]);
            /**
             * Activa cada sensor para que comience a tomar lecturas periódicas.
             * Si no habilitas el sensor, getValue() siempre devuelve 0 porque el sensor “no
             * está encendido”.
             */
            ds[i].enable(timeStep);
        }

        // Habilitamos la orientacion
        orientacion = getInertialUnit("orientacion");
        orientacion.enable(timeStep);

        // Habilitamos el GPS
        gps = getGPS("gps");
        gps.enable(timeStep);

    }

    /**
     * Hace que el tractor avance recto durante un tiempo determinado.
     * La velocidad y la dirección se pueden ajustar dentro del método.
     */
    /*
     * kk
     * public void avanzar() {
     * double velocidad = 5.0;
     * setCruisingSpeed(velocidad); // Avanzas velocidad Crucero
     * setSteeringAngle(0.0); // Angulo en el que giran las ruedas
     * esperar(2000);
     * }
     */

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
     * 
     * Realiza un giro controlado del tractor hasta alcanzar el ángulo
     * especificado.
     * Este método utiliza la lectura del sensor de orientación (InertialUnit) para
     * calcular el cambio acumulado en el ángulo de orientación del vehículo (yaw)
     * y continuar girando hasta que el tractor haya rotado exactamente el valor
     * indicado por parámetro.
     * 
     * @param angle Ángulo de giro deseado en radianes. Se interpreta como un valor
     *              relativo respecto a la orientación actual del tractor.
     */

    public void girar(double angle) {
        double startYaw = anguloActual(); // Ángulo inicial del tractor (radianes)
        double anguloRotado = 0.0; // Ángulo acumulado que el tractor ha girado

        setCruisingSpeed(5.0);

        /**
         * IMPORTANTE: setSteeringAngle(angle) no "coloca" el vehículo en un ángulo
         * absoluto,
         * sino que indica cuánto giran las ruedas mientras avanza (curvatura de la
         * trayectoria).
         */
        double steering;
        if (angle > 0) {
            steering = 0.5;
        } else {
            steering = -0.5;
        }
        // double steering = (angle > 0) ? 0.5 : -0.5;
        setSteeringAngle(steering);

        while (step(timeStep) != -1) {
            // Leo la orientacion actual a cada step y la normalizo para que este entre [-π,
            // π)
            double currentYaw = anguloActual();

            // Calcular el cambio angular desde el último paso
            double delta = Apoyo.normalizeAngle(currentYaw - startYaw);
            anguloRotado += delta;
            startYaw = currentYaw;

            // (opcional) debug en consola
            System.out.printf("Yaw actual: %.2f | Girado: %.2f/%.2f%n",
                    currentYaw, anguloRotado, angle);

            // Si ya alcanzamos el ángulo deseado, salimos del bucle
            if (Math.abs(anguloRotado) >= Math.abs(angle)) {
                break;
            }
        }

        // Detener el giro y enderezar las ruedas
        setSteeringAngle(0.0);
        setCruisingSpeed(0.0);
        esperar(200); // Pequeña pausa para estabilizar

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

    /**
     * termina si alguno de los DistanceSensor del vehículo detecta un obstáculo
     * a distancia menor que OBSTACLE_DISTANCE.
     * 
     * @return true si hay obstaculo, false si no lo hay
     */

    private boolean hasObstacle() {
        for (int i = 0; i < 2; i++) {
            if (ds[i].getValue() < 950.0)
                return true;
        }
        return false;
    }

    /**
     * Obtenemos el angulo en el que esta orientado el robot en la simulacion
     * 
     * @return angulo actual de las ruedas (eje y)
     */
    private double anguloActual() {
        double[] rpy = orientacion.getRollPitchYaw();
        return Apoyo.normalizeAngle(rpy[2]);
    }

}
