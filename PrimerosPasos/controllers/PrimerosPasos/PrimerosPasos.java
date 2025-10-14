
public class PrimerosPasos {
  public static void main(String[] args) {
    int timeStep = 32;

    Tractor tractor = new Tractor(timeStep);

    tractor.giro(3*Math.PI/2);
    tractor.giro(-5*Math.PI);
  }
}
