import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.InertialUnit;
import com.cyberbotics.webots.controller.RangeFinder;
import com.cyberbotics.webots.controller.vehicle.Car;

/**
 * Clase que representa un tractor autonomo en Webots.
 * Extiende de Car y contiene metodos para controlar su movimiento.
 */
public class TractorAutonomo extends Car {

    // Tiempo que tarda en ejecutar un movimiento en webots
    private int timeStep;

    // Valor maximo para que los distance sensor indiquen que hay un obstaculo
    private final double OBSTACLE_DISTANCE = 2000;

    // Maxima velocidad de los motores de las ruedas
    private double MAX_VELOCITY = 7.0;

    // Sensores de distancia del vehiculo. Tiene 2 en la parte delantera.
    private DistanceSensor[] ds;

    // Sensores de distancia2 del vehiculo.
    private RangeFinder rf;


    // Variable para sacar la rumbo del tractor
    private InertialUnit rumbo;

    /**
     * Proporciona las cordenadas del vehiculo
     */
    private GPS gps;

    /**
     * Constructor de TractorAutonomo.
     * Inicializa la instancia y obtiene el timeStep de la simulacion.
     */
    public TractorAutonomo() {
        super();
        timeStep = (int) getBasicTimeStep(); // Se corta la parte decima 1.9 = 1
        initSesores(); ///!FIJAR LAS CORDENADAS DEL TRACTOR AL ARRANCAR, TANGO ANGULOS, COMO POSICION
        esperar(500); // Da un tiempo para que se inicialicen los sensores.
    }

    private void initSesores() {
  
        // Habilitamos la rumbo
        rumbo = getInertialUnit("rumbo");
        rumbo.enable(timeStep);

        // Habilitamos el GPS
        gps = getGPS("gps");
        gps.enable(timeStep);

        //Habilitar RangeFinder
        rf= getRangeFinder("rf_front");
        rf.enable(timeStep);
    }

    /**
     * Detiene el tractor completamente usando freno realista.
     */
    public void frenar() {
        // Aplica freno al maximo
        setBrakeIntensity(1.0);
        setCruisingSpeed(0.0);
        setSteeringAngle(0.0); // ruedas rectas

        // Mantiene la simulacion unos pasos para asegurar que se detenga
        esperar(timeStep);

        // Desactiva el freno para permitir futuros movimientos
        setBrakeIntensity(0.0);
    }

    /**
     * Hace que el tractor retroceda durante 1 segundo,
     * pero se detiene inmediatamente si detecta un obstaculo trasero.
     */
    public void marchaAtras() {
        System.out.println("Iniciando marcha atras...");

        setSteeringAngle(0.0);
        setCruisingSpeed(-3.0); // velocidad negativa = marcha atras

        int tiempoTotal = 5000; // duracion total en ms
        int tiempoTranscurrido = 0;

        while (step(timeStep) != -1) {
            tiempoTranscurrido += timeStep;

            if (tiempoTranscurrido >= tiempoTotal) {
                break;
            }
        }

        frenar();
        System.out.println("Marcha atras completada sin obstaculos.");
    }

    /**
     * 
     * Realiza un giro controlado del tractor hasta alcanzar el angulo
     * especificado.
     * Este metodo utiliza la lectura del sensor de orientacion (InertialUnit) para
     * calcular el cambio acumulado en el angulo de orientacion del vehiculo (yaw)
     * y continuar girando hasta que el tractor haya rotado exactamente el valor
     * indicado por parametro.
     * 
     * @param angle angulo de giro deseado en radianes. Se interpreta como un valor
     *              relativo respecto a la orientacion actual del tractor.
     */

    public void girar(double angle) {

        double rumboInicial = rumboActual(); // Rumbo inicial del tractor (radianes)
        double rumboRotado = 0.0; // Rumbo acumulado que el tractor ha girado

        setCruisingSpeed(2.0);

        /**
         * IMPORTANTE: setSteeringAngle(angle) no "coloca" el vehiculo en un angulo
         * absoluto,
         * sino que indica cuanto giran las ruedas mientras avanza (curvatura de la
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
            if (hasObstacleFront()) {
                System.out.println("Obstaculo detectado. Deteniendo el tractor...");
                frenar();
                return; // Sale del metodo no solo del bucle while
            }
            // Leo la rumbo actual a cada step y la normalizo para que este entre [-π,
            // π)
            double rumboActual = rumboActual();

            // Calcular el cambio angular desde el ultimo paso
            double delta = Apoyo.normalizeAngle(rumboActual - rumboInicial);
            rumboRotado += delta;
            rumboInicial = rumboActual;

            // (opcional) debug en consola
            System.out.printf("Yaw actual: %.2f | Girado: %.2f/%.2f%n",
                    rumboActual, rumboRotado, angle);

            // Si ya alcanzamos el angulo deseado, salimos del bucle
            if (Math.abs(rumboRotado) >= Math.abs(angle)) {
                break;
            }
        }

        // Detener el giro y enderezar las ruedas
        setSteeringAngle(0.0);
        setCruisingSpeed(0.0);
        esperar(200); // Pequeña pausa para estabilizar

    }

    /**
     * Mantiene la simulacion activa durante el tiempo especificado.
     * 
     * @param durationMs Duracion en milisegundos
     */
    private void esperar(int durationMs) {
        int steps = (int) (durationMs / timeStep); // Calcula cuantos pasos tiene que dar a partir del tiempo
        for (int i = 0; i < steps; i++) {
            if (step(timeStep) == -1)
                break; // sale del bucle for si la simulacion termina
        }
    }

    /**
     * Hace avanzar el tractor a una velocidad especifica hasta recorrer una
     * distancia determinada.
     * El metodo controla el movimiento del tractor mediante el GPS para medir la
     * distancia recorrida
     * desde el punto de inicio hasta que alcanza la distancia objetivo.
     * 
     * @param velocidad Velocidad deseada para el avance en m/s. Si excede
     *                  MAX_VELOCITY,
     *                  se limita automaticamente a dicho valor maximo.
     * @param distancia Distancia objetivo a recorrer en metros. El metodo se
     *                  detendra
     *                  cuando el tractor haya recorrido esta distancia.
     */
    public void avanzar(double velocidad, double distancia) {
        double[] posicionInicial = gps.getValues();
        if (MAX_VELOCITY > velocidad)
            setCruisingSpeed(velocidad);
        else
            setCruisingSpeed(MAX_VELOCITY);

        while (step(timeStep) != -1) {

            if (hasObstacleFront()) {
                System.out.println("Obstaculo detectado. Deteniendo el tractor...");
                frenar();
                marchaAtras();
                return; // Sale del metodo no solo del bucle while
            }
            double[] posicionActual = gps.getValues();

            // Calcula cuanto se ha movido el robot desde su posicion inicial.
            double distanciaRecorrida = Apoyo.distance(posicionInicial, posicionActual);

            System.out.printf("Distancia recorrida: %.2f / %.2f m%n", distanciaRecorrida, distancia);

            if (distanciaRecorrida >= distancia) {
                break;
            }
        }

        frenar();
    }

    public void irAPosicion(double targetX, double targetY, double targetZ, double velocidad) {
        // 1. Obtener posicion actual (reutilizando verificacion GPS)
        double[] posicionActual = gps.getValues();
        double[] posicionObjetivo = { targetX, targetY, targetZ };

        System.out.printf("Posicion actual: [%.2f, %.2f, %.2f]%n",
                posicionActual[0], posicionActual[1], posicionActual[2]);
        System.out.printf("Posicion objetivo: [%.2f, %.2f, %.2f]%n",
                targetX, targetY, targetZ);

        // 2. Calcular distancia al objetivo (reutilizando logica de distancia)
        double distanciaAlObjetivo = Apoyo.distance(posicionActual, posicionObjetivo);
        System.out.printf("Distancia al objetivo: %.2f metros%n", distanciaAlObjetivo);

        // Si ya estamos muy cerca, no hacer nada
        if (distanciaAlObjetivo < 0.3) {
            System.out.println("Ya esta en la posicion objetivo o muy cerca");
            return;
        }

        // 3. Calcular angulo hacia el objetivo
        double anguloHaciaObjetivo = Apoyo.calcularAnguloHaciaObjetivo(posicionActual, posicionObjetivo);
        System.out.printf("angulo hacia el objetivo: %.2f radianes (%.2f grados)%n",
                anguloHaciaObjetivo, Math.toDegrees(anguloHaciaObjetivo));

        System.out.println("------------------------------------\n --------------------\n----------------");
        System.out.println("Angulo hacia el objetivo = " + anguloHaciaObjetivo);
        System.out.println("------------------------------------\n --------------------\n----------------");

        // 4. Orientarse hacia el objeto
        double rumboActual = rumboActual();
        double diferenciaAngular = Apoyo.normalizeAngle(anguloHaciaObjetivo - rumboActual);

        System.out.printf("Orientacion actual: %.2f rad | Diferencia angular: %.2f rad%n",
                rumboActual, diferenciaAngular);

        // Solo girar si la diferencia es significativa
        if (Math.abs(diferenciaAngular) > 0.05) { // Umbral de ~2.8 grados
            System.out.println("Girando hacia el objetivo...");
            girar(diferenciaAngular);
        } else {
            System.out.println("Ya esta orientado hacia el objetivo");
        }

        // 5. Avanzar hasta la posicion objetivo
        System.out.println("Avanzando hacia el objetivo...");
        avanzar(velocidad, distanciaAlObjetivo);

        System.out.println("¡Posicion objetivo alcanzada!");

    }

    private boolean hasObstacleFront() {
    float[] depth = rf.getRangeImage();
    int center = depth.length / 2;

    int window = 5;  // píxeles alrededor del centro
    for (int i = center - window; i <= center + window; i++) {
        if (depth[i] < OBSTACLE_DISTANCE)
            return true;
    }
    return false;
}


    /**
     * Obtenemos el angulo en el que esta orientado el robot en la simulacion
     * 
     * @return angulo actual de las ruedas (eje y)
     */
    private double rumboActual() {
        double[] rpy = rumbo.getRollPitchYaw();
        return Apoyo.normalizeAngle(rpy[2]);
    }

}
