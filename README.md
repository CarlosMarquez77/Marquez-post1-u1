# Marquez-post1-u1
Post-contenido — Refactorización SOLID y análisis de patrones GoF en Spring

# Análisis de Violaciones SOLID

| Principio | Método/Sección afectada | Descripción de la violación |

SRP: calculateTotal + applyDiscount + saveOrder + sendEmail + printReport | La clase OrderProcessor tiene cinco responsabilidades distintas (cálculo de totales, aplicación de descuentos, persistencia, notificación por correo y generación de reportes). Un cambio en cualquiera de ellas obliga a modificar la misma clase, aumentando el riesgo de introducir errores no relacionados con el cambio original.

OCP: applyDiscount (if/else sobre customerType) | Agregar un nuevo tipo de cliente (por ejemplo, PREMIUM) requiere editar directamente el método applyDiscount en lugar de extender el comportamiento mediante nuevas clases, violando el principio de "abierto a extensión, cerrado a modificación".

DIP: Toda la clase (dependencias internas sin abstracciones) | OrderProcessor contiene toda la lógica de negocio, persistencia y notificación sin depender de abstracciones (interfaces). Esto impide sustituir, por ejemplo, el mecanismo de persistencia o de notificación por otra implementación sin reescribir la clase completa.