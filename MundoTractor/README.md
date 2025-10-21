# Webots

**Paso 1:** en Webots, `File->New->New project directory`. En nuestro caso, hemos elegido de nombre `MundoTractor`. En el diálogo que aparece, hemos elegido como nombre del mundo `MiniMundo` y hemos activado la opción `Add a rectangle arena` 

**Paso 2:** Creamos un controlador para nuestro tractor `File->New->New Robot Controller` seleccionamos `Java` y el nombre de nuestro controlador sera `ControladorTractor`.



## Crear un proto Tractor:

**Objetivo:**  
Para añadir sensores (como GPS) a un prototipo (proto) de tractor, es necesario editar su archivo fuente.

**Pasos realizados:**

**1. Copia del archivo fuente:**  
Se copió el archivo fuente del tractor desde un ejemplo en Webots donde aparece la clase tractor (`C:\Program Files\Webots\projects\vehicles\worlds\boomer.wbt`)  y desde donde sacamos la pagina web del archivo fuente de la clase tractor `https://raw.githubusercontent.com/cyberbotics/webots/R2025a/projects/vehicles/protos/generic/Tractor.proto`. Este archivo lo pegamos a un archivo nuevo `.proto` en la carpeta `protos` de nuestro proyecto (`C:\Users\Antonio\Desktop\UPM\TFG\webots\MundoTractor\protos`). El nombre del archivo debe ser el mismo que protoname. En nuestro caso **TractorAutonomo**.

Una vez hecho esto modificaremos el archivo para adactarlo a nuestro contexto.



**2. Actualización de las rutas (EXTERNPROTO):**  
Se editaron las rutas de los directorios dentro del archivo, ya que por defecto apuntaban a las instalaciones de Webots. Se adaptaron estas rutas para que funcionen en el proyecto actual. Una solución fue descargar los recursos directamente desde el repositorio Git de Webots:

`EXTERNPROTO "https://raw.githubusercontent.com/cyberbotics/webots/released/projects/vehicles/protos/abstract/Car.proto" EXTERNPROTO "https://raw.githubusercontent.com/cyberbotics/webots/released/projects/appearances/protos/MattePaint.proto" EXTERNPROTO "https://raw.githubusercontent.com/cyberbotics/webots/released/projects/vehicles/protos/generic/TractorFrontWheel.proto" EXTERNPROTO "https://raw.githubusercontent.com/cyberbotics/webots/released/projects/vehicles/protos/generic/TractorRearWheel.proto"`

Antes del siguiente paso, adaptamos nuestro proto al contexto del proyecto:

- Descargar la siguiente imagen y colocarla en un nuevo directorio:
  
  - Imagen: [steering_wheel.png](https://raw.githubusercontent.com/cyberbotics/webots/released/projects/vehicles/protos/generic/textures/steering_wheel.png)
  
  - Directorio: `C:/Users/Antonio/Desktop/UPM/TFG/webots/MundoTractor/protos/textures/steering_wheel.png`

- Añadir el `EXTERNPROTO` de nuestro tractor al mundo `EvitrObstaculos.wbt`:

        `EXTERNPROTO "../protos/tractorAutonomo.proto"`

**3. Edición del contenido:**  
Una vez solucionado el problema de las rutas, se modificó el archivo para integrar los nuevos sensores y dispositivos necesarios.

El archivo proto indica que hay un campo en el que todo lo que se añada se insertará en el tractor:

`# ******************* # field MFNode sensorSlot [ ]  # Extends the robot with new nodes at the center of the vehicle. # *******************`

Esto significa que todo lo que se agregue dentro de ese slot se colocará en la posición central del vehículo, pudiendo añadir sensores, GPS, etc., sin modificar la estructura del tractor.

**4. Añadir sensores desde Webots:**  
Se pulsa `Add` sobre `MFNode sensorSlot [ ]` y se añaden tres tipos de sensores:

- **DistanceSensor:** tres sensores en total (dos frontales: `ds_right` y `ds_left`, y uno trasero: `ds_trasero`).

- **GPS**

- **InternalUnit**

Para los sensores de distancia, se recomienda colocarlos ligeramente por delante del tractor para detectar obstáculos con suficiente antelación y mejorar la detección de colisiones.



# vsCode

1º Abrimos la carpeta del controler ` \MundoTractor\controllers\ControladorTractor` una vez abierta lo primero que aremos seran añadir los jar (controller.jar, vehicle.jar, antlr-4.13.2-complete.jar).

Em vsCode JavaProject -> ControladorTractor -> Referenced Libreries

---

Vamos a generar un codigo básico usando solo la libreria controller tratando a nuestro robot como si fuera un robot. 

Para compilar:

```powershell
javac -cp "C:\Program Files\Webots\lib\controller\java\Controller.jar;C:\Program Files\Webots\lib\controller\java\vehicle.jar;." *.java 
```

**Controladores:**  
En VSCode, dentro de la carpeta `Controller`, se crean los controladores que permiten que el robot se mueva.

- **TractorAutonomo.java:** contiene todos los métodos necesarios para el funcionamiento del robot, como girar, avanzar, etc., e inicializa todos los sensores. Esta clase utiliza la clase `Apoyo` para centralizar métodos que se repiten, reduciendo código redundante y manteniendo la clase más organizada.

- **ControladorTractor.java:** clase principal donde se instancia un objeto de `TractorAutonomo`, lo que permite acceder a los métodos como `avanzar`.

- **Apoyo.java:** Contiene todos los metodos que se utilizan varias veces dentro de la clase `TractorAutonomo` y que se pueden llevar a una clase externa.

La compilación se realiza incluyendo las librerías `controller.jar` y `vehicle.jar`:
