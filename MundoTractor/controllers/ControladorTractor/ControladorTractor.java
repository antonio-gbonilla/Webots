public class ControladorTractor {

  public static void main(String[] args) {
    // Crear el tractor autónomo
    TractorAutonomo tractor = new TractorAutonomo();

    //DetectarSensores.detectar();

    tractor.avanzar(10, 20);
    tractor.girar(Math.PI/2);
  }
}
