# SAS (Simple Automaton Simulator)

![image](https://github.com/opDataa/Simple-Automaton-Simulator/assets/62800196/a0264a11-f37d-4a1b-9dc1-4fdf7a2d2b29)

## Descripción
El proyecto Simple-Automaton-Simulator es una implementación de un simulador de autómatas finitos deterministas (DFA) y no deterministas (NFA). La arquitectura del proyecto sigue el patrón de diseño Modelo-Vista-Controlador (MVC) para una mejor organización y mantenimiento del código.

Para una comprensión más detallada de su funcionamiento, consulte el [SAS-Reference.pdf](ruta/al/pdf/SAS-Reference.pdf) .

## Características

### Modelado de Autómatas
- **AbstractAutomata.java**: Clase abstracta que define las estructuras y métodos básicos para manejar los estados y transiciones de un autómata.
- **AutomataDeterminista.java**: Implementación específica para autómatas deterministas.
- **AutomataNoDeterminista.java**: Implementación específica para autómatas no deterministas.
- **Transaction.java**: Clase que representa una transición entre estados.

### Vista
- **ControlPane.java**: Panel de control que incluye un campo de texto para entrada y botones para manipular la visualización del autómata.
  
<p align="center">
  <img width="363" alt="ui automaton" src="https://github.com/opDataa/Simple-Automaton-Simulator/assets/62800196/c43bb32c-3492-474e-aa8c-7a855675083d">
</p>

### Controlador
- **ControladorTextInput.java**: Gestiona la entrada de texto y su visualización durante el modo "paso a paso".
- **ControladorAutomata.java**: Controlador principal que maneja las acciones y la lógica del autómata.

## Ejemplos
Se incluyen varios ejemplos para ilustrar el funcionamiento del simulador tanto en modo directo como en modo paso a paso:
- Ejemplo 1
- Ejemplo 2
- Ejemplo 3
- Ejemplo 4
- EjemploPropuesto1 
- EjemploPropuesto2
- EjemploPropuesto3 (contador)
- EjemploPropuesto4 (regexp HTML)

## Instrucciones de Uso

### Importar Autómata
Los autómatas se pueden importar desde archivos .txt ubicados en la carpeta `dataset`.

### Ejecutar el Simulador
- **Modo directo**: Muestra la solución del autómata inmediatamente.
- **Modo paso a paso**: Muestra la transición entre estados de forma interactiva.

### Interacción
- Uso de botones en el `ControlPane` para manipular la vista del autómata.
- Flechas del teclado para navegar en el modo paso a paso.

## Dependencias
Este proyecto utiliza la librería GraphStream para la representación gráfica de los autómatas. Asegúrese de tenerla instalada y configurada correctamente.

## Problemas Conocidos
- **Visualización de Transiciones**: Las transiciones y los iconos pueden no mostrarse correctamente en algunos casos debido a limitaciones de la librería GraphStream. Usar el botón de zoomOut como solución temporal.
- **ControladorTextInput**: Puede presentar errores al intentar retroceder, pero el resto de las funciones operan correctamente.

## Observaciones Finales
- La programación de los algoritmos gráficos requirió un esfuerzo significativo, no tanto por su complejidad, sino por la falta de experiencia previa.
- La documentación del código está mayormente en inglés, pero algunos comentarios pueden estar en español.

## Estructura del Proyecto
- **Modelo**: Clases encargadas de la lógica de los autómatas.
- **Vista**: Componentes visuales para la interacción con el usuario.
- **Controlador**: Gestión de la lógica y coordinación entre el modelo y la vista.
