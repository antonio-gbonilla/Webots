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

        /**
     * Calcula la distancia entre dos puntos representados por arrays de tres
     * coordenadas.
     * 
     * @param start array eje xyz de la posicion inicial
     * @param end   array eje xyz de la posicion final
     * @return
     */
    public static double distance(double[] start, double[] end) {
        double x2 = (start[0] - end[0]) * (start[0] - end[0]);
        double y2 = (start[1] - end[1]) * (start[1] - end[1]);
        double z2 = (start[2] - end[2]) * (start[2] - end[2]);
        return Math.sqrt(x2 + y2 + z2);
    }

}