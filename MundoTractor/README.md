**Paso 1:** en Webots, `File->New->New project directory`. En nuestro caso, hemos elegido de nombre `MundoTractor`. En el diálogo que aparece, hemos elegido como nombre del mundo `MiniMundo` y hemos activado la opción `Add a rectangle arena` 

**Paso 2:** Creamos un controlador para nuestro tractor `File->New->New Robot Controller` seleccionamos `Java` y el nombre de nuestro controlador sera `ControladorTractor`.

Respecto al mundo lo dejamos igual pero hacemos mas grande el rectangle area y añadimos el proto del tractor (Add a node -> Proto nodes (webots Projects) -> vehicles -> generic -> Tractor (Robot)) una vez añadido le asociamos el ControladorTractor. 

# vsCode

1º Abrimos la carpeta del controler ` \MundoTractor\controllers\ControladorTractor` una vez abierta lo primero que aremos seran añadir los jar (controller.jar, vehicle.jar, antlr-4.13.2-complete.jar).

Em vsCode JavaProject -> ControladorTractor -> Referenced Libreries

---

Vamos a generar un codigo básico usando solo la libreria controller tratando a nuestro robot como si fuera un robot. 

Para compilar:

```powershell
javac -cp "C:\Program Files\Webots\lib\controller\java\Controller.jar;C:\Program Files\Webots\lib\controller\java\vehicle.jar;." *.java 
```
