import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.InertialUnit;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;


public class Tractor extends Robot {

    /**
	 * Valor máximo para que los DistanceSensor indiquen que
	 * detectan un obstáculo.
	 */
	private final double OBSTACLE_DISTANCE=950.0;


    //Máxima velocidad de los motores de las ruedas.
	private double maxVelocity = 10.0;


    /**
	 * Intervalo de tiempo utilizado en la simulación. Lo fija el Controller y lo recibe
	 * el vehículo a través del constructor. Tiene que ser una potencia de 2. Valores menores de 32
	 * dan lugar a errores de cálculo. Se mide en milisegundos.
	 */
	private int timeStep;

    /**
	 * Sensores de distancia del vehículo. Tiene 2 en la parte delantera.
	 */
    private DistanceSensor [] ds;

    /**
	 * Motores de las 4 ruedas
	 */
    private Motor [] wheels;

    /**
     * Variable para sacar la orientacion del tractor
     */
    private InertialUnit orientacion;

    public Tractor (int timeStep){
        this.timeStep=timeStep;
        initSensores();
        initMotors();
        step();
    }

    /**
	 * Inicializa los sensores del vehículo.
	 */

    protected void initSensores(){
        ds=new DistanceSensor[2];
        String [] nombreDS = {"ds_right", "ds_left"};
        for (int i=0; i<ds.length; i++ ){
            ds[i] = this.getDistanceSensor(nombreDS[i]);
            /**
             * Activa cada sensor para que comience a tomar lecturas periódicas.
             * Si no habilitas el sensor, getValue() siempre devuelve 0 porque el sensor “no está encendido”.
             */
            ds[i].enable(timeStep);
        }

        orientacion=this.getInertialUnit("orientacion");
        orientacion.enable(timeStep);
    }

    /**
	 * Inicializa los cuatro motores de las ruedas.
	 */
    protected void initMotors(){
        wheels = new Motor[4];
        String [] nombreWheels = {"ruedaDelanteraIzquierda","ruedaDelanteraDerecha",
        "ruedaTraseraIzquierda","ruedaTraseraDerecha"};
        for(int i=0; i<wheels.length; i++){
            wheels[i] = this.getMotor(nombreWheels[i]); //Obtengo el motor correspondiente de cada robot
            /**
             * En Webots, los motores pueden trabajar en dos modos:

                Control de posición: el motor gira hasta alcanzar un ángulo objetivo. Es el modo que esta por defecto, las ruedas giran
                Un numero determinado de radianes por ejemplo seria lago asi (wheel.setPosition(Math.PI) -> Las ruedas giran una vuelta).

                Control de velocidad: Hay que usarlo cuando quieres que el motor gire continuamente (en nuestro proyecto siempre porque
                simularian las ruedas de un coche (Ruedas, hélices, cintas, ventiladores)).

                Cuando pones la posición en Double.POSITIVE_INFINITY, desactivas el control de posición → el motor queda en modo de velocidad.
                Esto es lo que queremos para las ruedas de un robot móvil.
             */

            wheels[i].setPosition(Double.POSITIVE_INFINITY);
            
            wheels[i].setVelocity(0.0); //Pones una velocidad en particular.
        }
    }

    /**
     * Avanza la simulacion un intervalo
     * Llama al método step(int) heredado de Robot, usando el timeStep que tienes almacenado en tu clase.
     * Esto permite inicilizarlo externamente desde un main.
     */
    private void step (){
        this.step(timeStep);
    }


    public double getMaxVelocity() {
        return maxVelocity;
    }
    
    /**
     * Tanto en getLeftVelocity como en getRightVelocity no hace falta devolver las dos ruedas porque en ambas esta el mismo valor
     * A si que tanto la delatera como la trasera giran a la misma velocidad.
     * 
     */

    public double getLeftVelocity() {
		return wheels[0].getVelocity();
	}

	public double getRightVelocity() {
		return wheels[1].getVelocity();
	}


    /** 
     * Pone una velocidad a las ruedas de la parte derecha
     * Luego llamas a step() para que la simulacion avance y hagas cambios
    */

    private void setLeftVelocity (double v){
        wheels[0].setVelocity(v);
        wheels[2].setVelocity(v);
        step();
    }

    /** 
     * Pone una velocidad a las ruedas de la parte derecha
     * Luego llamas a step() para que la simulacion avance y hagas cambios
    */
    private void setRightVelocity (double v){
        wheels[1].setVelocity(v);
        wheels[3].setVelocity(v);
        step();
    }

    /**
	 * Establece la misma velocidad en las cuatro ruedas.
	 * @param v Múltiplo de la maxVelocity. Por ejemplo, v=1.0 equivale
	 * a establecer la velocidad máxima, v=0.5 establece la velocidad en 
	 * un 50% de la velocidad máxima y v=2.0 establece la velocidad en el doble de
	 * la velocidad máxima.
	 */
	public void setVelocity(double v) {
		setLeftVelocity(v);
		setRightVelocity(v);
	}

    /**
     * Metodo para girar hacia la derecha ponemos que las ruedas izquierda que vayan hacia delante y las derchas van hacia atras
     */

    public void giroDerecha (){
        setLeftVelocity(1);
        setRightVelocity(-1);
    }

    
    /**
     * Metodo para girar hacia la derecha ponemos que las ruedas derecha que vayan hacia delante y las izquierda van hacia atras
     */

    public void giroIzquierda(){
        setLeftVelocity(-1);
        setRightVelocity(1);
    }
    
    /**
     * Metodo que para al robot, pone la velocidad de los motores a 0
     */

    public void stop (){
        setLeftVelocity(0);
        setRightVelocity(0);
    }


    /**
     * Este método hace que el robot gire hacia la derecha un ángulo dado (en radianes) usando el InertialUnit.
     * @param angle parametro en rad que indica el angulo que gira el tractor
     * 
    */

    private void giroDerecha(double angle) {
        // ángulo a girar en radianes (positivo)
        //Pasamos los angulos de grados (mas sencillo para el usuario) a radianes
        //double angleRad = Math.toRadians(angle);
        //Obtenemos el angulo en el que esta orientado el robot en la simulacion
        double [] rpy = orientacion.getRollPitchYaw();
        double starAngle = Apoyo.normalizeAngle(rpy[2]);   

        //Rotacion acumalada de rotacion le iremos sumando cuandto gira el tractor en cada step
        double rotacionAngle = 0.0;
        double increment = 0.0;

        // empezar a girar
        setLeftVelocity(0.2 * maxVelocity);
        setRightVelocity(-0.2 * maxVelocity);

        while (step(timeStep) != -1) {
            double currentAngle = Apoyo.normalizeAngle(orientacion.getRollPitchYaw()[2]);
            double diferencia = currentAngle - starAngle;

            // corregir el salto de -π a π
            diferencia=Apoyo.normalizeAngle(diferencia);

            increment = diferencia;
            rotacionAngle += increment;
            starAngle = currentAngle;

            //Comprobar si hemos alzandado el angulo deseado, prefiero hacerlo asi con el while step, que como condicion
            if (Math.abs(rotacionAngle) >= angle) {
                break;
            }
        }

        stop();
    }


    private void giroIzquierda(double angle) {
        double [] rpy = orientacion.getRollPitchYaw();
        double starAngle = Apoyo.normalizeAngle(rpy[2]);   

        double rotacionAngle = 0.0;
        double increment = 0.0;

        // Lo unico que varia es la orientacion
        setLeftVelocity(-0.2 * maxVelocity);
        setRightVelocity(0.2 * maxVelocity);

        while (step(timeStep) != -1) {
            double currentAngle = Apoyo.normalizeAngle(orientacion.getRollPitchYaw()[2]);
            double diferencia = currentAngle - starAngle;

            diferencia=Apoyo.normalizeAngle(diferencia);

            increment = diferencia;
            rotacionAngle += increment;
            starAngle = currentAngle;

            if (Math.abs(rotacionAngle) >= angle) {
                break;
            }
        }

        stop();
    }

    public void giro(double angle) {
        if (angle > 0) {
            System.out.println("angulo cuando es positivo");
            giroDerecha(angle);
        } else {
            System.out.println("angulo cuando es negatico");
            giroIzquierda(-angle);
        }
    }

}
