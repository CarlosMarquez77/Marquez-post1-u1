# Marquez-post1-u1
Post-contenido — Refactorización SOLID y análisis de patrones GoF en Spring

# Análisis de Violaciones SOLID

| Principio | Método/Sección afectada | Descripción de la violación |
|-----------|-------------------------|-----------------------------|
| SRP | calculateTotal + applyDiscount + saveOrder + sendEmail + printReport | La clase OrderProcessor tiene cinco responsabilidades distintas (cálculo de totales, aplicación de descuentos, persistencia, notificación por correo y generación de reportes). Un cambio en cualquiera de ellas obliga a modificar la misma clase, aumentando el riesgo de introducir errores no relacionados con el cambio original. |
| OCP | applyDiscount (if/else sobre customerType) | Agregar un nuevo tipo de cliente (por ejemplo, PREMIUM) requiere editar directamente el método applyDiscount en lugar de extender el comportamiento mediante nuevas clases, violando el principio de "abierto a extensión, cerrado a modificación". |
| DIP | Toda la clase (dependencias internas sin abstracciones) | OrderProcessor contiene toda la lógica de negocio, persistencia y notificación sin depender de abstracciones (interfaces). Esto impide sustituir, por ejemplo, el mecanismo de persistencia o de notificación por otra implementación sin reescribir la clase completa. |


## Parte 1 — Refactorización SOLID
Proyecto Maven que refactoriza OrderProcessor aplicando SRP, OCP y
DIP. Ver parte-1-refactorizacion-solid/.

## Parte 2 — Análisis de Patrones GoF en Spring
| # | Patrón | Categoría | Clase en Spring |
|---|--------|-----------|-----------------|
| 1 | Singleton | Creacional | DefaultSingletonBeanRegistry |
| 2 | Proxy | Estructural | JdkDynamicAopProxy |
| 3 | Template Method | Comportamiento | JdbcTemplate |

Ver parte-2-analisis-gof-spring/documento-analisis.md.

## Herramientas utilizadas
- Java 17+, Apache Maven, VS Code, Git, GitHub
- Código fuente de Spring Framework (investigación)

## Conclusiones
Esta actividad fue un buen ejercicio tanto de programación como de paciencia, ya que
antes de poder escribir código tocó instalar y configurar bien las herramientas
(Git, Java y Maven), lo cual tomó más tiempo del esperado por conflictos entre
versiones de Java instaladas en el computador. En cuanto al ejercicio en sí, quedó
claro que tener una clase que hace "de todo" (como OrderProcessor al inicio) genera
problemas a futuro, y que separar las responsabilidades en clases más pequeñas hace
el código más fácil de entender y de modificar. Además, investigar cómo Spring
Framework usa estos mismos conceptos internamente ayudó a ver que no son solo teoría
de clase, sino prácticas que se usan de verdad en herramientas que millones de
programadores utilizan a diario. En general, la actividad dejó una idea clara: escribir
código ordenado desde el principio ahorra muchos problemas más adelante.