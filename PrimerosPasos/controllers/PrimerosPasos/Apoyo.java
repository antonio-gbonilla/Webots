public class Apoyo {

    /**
     * Este método toma un ángulo en radianes y lo “pone” dentro del rango [-π, π),
     * que es el rango que queremos para trabajar con el yaw de Webots.
     * 
     * @param angle ángulo en radianes
     * @return valor del angulo normalizado
     * 
     */
    public static double normalizeAngle(double angle) {
        // Normaliza el ángulo a [-π, π)

        while (angle >= Math.PI)
            angle -= 2.0 * Math.PI; // si es mayor que pi
        while (angle < -Math.PI)
            angle += 2.0 * Math.PI; // si es menor que pi
        return angle;
    }

}
