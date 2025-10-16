public class ControladorTractor {

  public static void main(String[] args) {
    // Crear el tractor autónomo
    TractorAutonomo tractor = new TractorAutonomo();

    // Ejecutar el comportamiento
    tractor.avanzar(); // se mueve recto
    tractor.girar(45);
    tractor.marchaAtras();
    tractor.avanzar();
    tractor.frenar();
  }
}
