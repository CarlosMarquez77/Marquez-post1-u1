# Análisis de Patrones de Diseño GoF en Spring Framework

## Portada

**Nombre:** Carlos Daniel Márquez Carreño
**Código:** 02240131057
**Curso:** Patrones de Diseño de Software-A1
**Unidad:** 1 — Fundamentos de Patrones de Diseño y Buenas Prácticas
**Fecha:** 16 de agosto de 2026

---

## Introducción

Este documento presenta un análisis de tres patrones de diseño del catálogo GoF (Gang of Four) identificados en el código fuente de Spring Framework, uno de los frameworks de desarrollo Java más utilizados en la industria. El objetivo es reconocer cómo un framework maduro y ampliamente adoptado aplica de manera sistemática patrones de diseño reconocidos para resolver problemas recurrentes de arquitectura de software, y conectar estas decisiones de diseño con los principios SOLID (Gamma et al., 1994).

Spring Boot se utiliza como caso de estudio por ser una de las bases más comunes para el desarrollo de aplicaciones empresariales en Java. Su núcleo (contenedor de inversión de control, gestión de beans, soporte de programación orientada a aspectos y acceso a datos) está construido sobre patrones de diseño clásicos, lo que lo convierte en un caso de estudio idóneo para observar estos patrones aplicados en un contexto de producción real, en lugar de ejemplos académicos simplificados (Spring Framework, 2026).

---

## Análisis de Patrón 1: Singleton

El patrón Singleton pertenece a la categoría **Creacional** del catálogo GoF (Gamma et al., 1994). Su propósito general es garantizar que una clase tenga una única instancia y proporcionar un punto de acceso global a ella, evitando la creación de múltiples objetos cuando el diseño requiere que exista solo uno compartido por toda la aplicación.

En Spring Framework, este patrón aparece en la clase `DefaultSingletonBeanRegistry`, ubicada en el paquete `org.springframework.beans.factory.support`, dentro del módulo `spring-beans` (Spring Framework, 2026). Esta clase sirve como base para la gestión de instancias de beans dentro del contenedor de inversión de control (IoC).

El problema que resuelve en este contexto es evitar la creación repetida de objetos costosos de inicializar (por ejemplo, servicios que abren conexiones, cargan configuración o inicializan recursos pesados) cada vez que son solicitados por distintas partes de la aplicación. En lugar de que cada componente cree su propia instancia, Spring gestiona un registro centralizado donde cada bean, identificado por su nombre, se crea una única vez y se comparte entre todos los componentes que lo requieran. Esta solución es preferible a que cada clase implemente su propio mecanismo de instancia única, porque centraliza la responsabilidad de gestión del ciclo de vida en el contenedor, en lugar de dispersarla en cada clase de negocio.

Como evidencia de código, se identificó el mapa `singletonObjects`, una estructura `ConcurrentHashMap` que asocia el nombre de cada bean con su instancia única (Spring Projects, 2026):

```java
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
    Assert.notNull(beanName, "Bean name must not be null");
    Assert.notNull(singletonObject, "Singleton object must not be null");
    this.singletonLock.lock();
    try {
        addSingleton(beanName, singletonObject);
    }
    finally {
        this.singletonLock.unlock();
    }
}
```

El método `registerSingleton()` inserta un bean en el registro utilizando un bloqueo (`singletonLock`) que garantiza que, incluso bajo acceso concurrente de múltiples hilos, cada bean quede registrado exactamente una vez.

Este patrón refuerza principalmente el principio de **Responsabilidad Única (SRP)**, ya que separa la responsabilidad de creación y gestión del ciclo de vida de los objetos (a cargo del contenedor IoC) de la responsabilidad de uso de esos objetos (a cargo de las clases de negocio), que simplemente reciben la instancia ya creada sin preocuparse de cómo o cuándo fue construida.

---

## Análisis de Patrón 2: Proxy

El patrón Proxy pertenece a la categoría **Estructural** del catálogo GoF (Gamma et al., 1994). Su propósito general es proporcionar un objeto sustituto o intermediario que controla el acceso a otro objeto, permitiendo ejecutar lógica adicional antes o después de que la llamada llegue al objeto real, sin que este último tenga conocimiento de dicha intermediación.

En Spring Framework, este patrón aparece en la clase `JdkDynamicAopProxy`, ubicada en el paquete `org.springframework.aop.framework`, dentro del módulo `spring-aop` (Spring Framework, 2026). Esta clase es una de las dos implementaciones principales que Spring AOP utiliza para crear proxies dinámicos (la otra es basada en CGLIB).

El problema que resuelve en este contexto es permitir la implementación de aspectos transversales (cross-cutting concerns) —como registro de logs, control transaccional, seguridad o caché— sin modificar el código de las clases de negocio originales. Si cada clase tuviera que implementar manualmente esta lógica adicional, se violaría la separación de responsabilidades y se duplicaría código en toda la aplicación. En lugar de eso, Spring genera dinámicamente un proxy que implementa las mismas interfaces que el objeto real e intercepta cada llamada a método, insertando la lógica adicional de forma transparente.

Como evidencia de código, se identificó la declaración de la clase y su método central de interceptación (Spring Projects, 2026):

```java
final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {

    private final AdvisedSupport advised;

    public @Nullable Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object oldProxy = null;
        boolean setProxyContext = false;
        TargetSource targetSource = this.advised.targetSource;
        Object target = null;
        // ...continúa la lógica de interceptación...
    }
}
```

La clase implementa `InvocationHandler`, la interfaz estándar de Java para interceptar llamadas a métodos sobre un proxy dinámico. El campo `targetSource` permite acceder al objeto real que se está proxificando, mientras que el método `invoke(Object proxy, Method method, Object[] args)` es el punto donde Spring intercepta cada invocación antes de delegarla al objeto original.

Este patrón refuerza principalmente el principio de **Abierto/Cerrado (OCP)**, ya que permite añadir comportamiento adicional (logging, transacciones, seguridad) a una clase existente sin modificar su código fuente: el comportamiento se extiende mediante composición y proxies, no mediante edición directa de la clase original.

---

## Análisis de Patrón 3: Template Method

El patrón Template Method pertenece a la categoría **de Comportamiento** del catálogo GoF (Gamma et al., 1994). Su propósito general es definir el esqueleto de un algoritmo en una operación, delegando la implementación de algunos de sus pasos a las subclases o a callbacks, sin alterar la estructura general del algoritmo.

En Spring Framework, este patrón aparece en la clase `JdbcTemplate`, ubicada en el paquete `org.springframework.jdbc.core`, dentro del módulo `spring-jdbc` (Spring Framework, 2026). Esta clase es el punto central de acceso a bases de datos mediante JDBC dentro del ecosistema Spring.

El problema que resuelve en este contexto es evitar que cada desarrollador tenga que reescribir manualmente el flujo repetitivo y propenso a errores de JDBC: abrir una conexión, crear un statement, ejecutar la operación, manejar excepciones SQL y finalmente cerrar los recursos en el orden correcto. Este flujo es prácticamente idéntico en cualquier operación JDBC, mientras que lo único que cambia es la consulta específica a ejecutar y el procesamiento de su resultado. En lugar de duplicar este flujo en cada punto de acceso a datos, `JdbcTemplate` lo centraliza y delega únicamente la parte variable a través de una interfaz de callback.

Como evidencia de código, se identificó el método `execute` (Spring Projects, 2026):

```java
private <T extends @Nullable Object> T execute(StatementCallback<T> action, boolean closeResources) throws DataAccessException {
    Assert.notNull(action, "Callback object must not be null");
    Connection con = DataSourceUtils.getConnection(obtainDataSource());
    Statement stmt = null;
    try {
        stmt = con.createStatement();
        applyStatementSettings(stmt);
        T result = action.doInStatement(stmt);
        handleWarnings(stmt);
        return result;
    }
    catch (SQLException ex) {
        if (stmt != null) {
            handleWarnings(stmt, ex);
        }
        String sql = getSql(action);
        JdbcUtils.closeStatement(stmt);
        stmt = null;
        DataSourceUtils.releaseConnection(con, getDataSource());
        con = null;
        throw translateException("StatementCallback", sql, ex);
    }
    finally {
        if (closeResources) {
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.releaseConnection(con, getDataSource());
        }
    }
}
```

El flujo fijo —obtener conexión, crear el statement, manejar advertencias, capturar y traducir excepciones, y liberar recursos— está completamente controlado por `JdbcTemplate`. El único paso variable es `action.doInStatement(stmt)`, delegado al callback `StatementCallback` que el desarrollador implementa según la operación específica que necesite ejecutar.

Este patrón refuerza principalmente el principio de **Inversión de Dependencias (DIP)**, ya que el flujo fijo de `JdbcTemplate` no depende de una implementación concreta de operación JDBC, sino de la abstracción `StatementCallback`. Esto permite que el comportamiento variable se inyecte desde fuera, sin que la clase central conozca los detalles concretos de cada operación.

---

## Conclusiones

El análisis de estos tres patrones en Spring Framework evidencia que su presencia en frameworks maduros no es incidental, sino el resultado de decisiones de diseño deliberadas orientadas a resolver problemas recurrentes de gestión de instancias, extensión de comportamiento y reducción de código repetitivo (Gamma et al., 1994). Cada patrón identificado —Singleton, Proxy y Template Method— está directamente conectado con uno o más principios SOLID, lo que confirma que los patrones de diseño y los principios SOLID no son conceptos aislados, sino herramientas complementarias: los principios orientan el "por qué" de una decisión de diseño, mientras que los patrones ofrecen el "cómo" concreto de implementarla. Esta relación es una lección directa para el diseño de software propio: identificar el principio que se está buscando reforzar (separación de responsabilidades, extensibilidad, bajo acoplamiento) ayuda a reconocer qué patrón de diseño resulta más adecuado para el problema en cuestión.

---

## Referencias

Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design patterns: Elements of reusable object-oriented software*. Addison-Wesley.

Spring Framework. (2026). *Spring Framework documentation*. VMware. https://docs.spring.io/spring-framework/reference/

Spring Projects. (2026). *spring-framework* [Código fuente]. GitHub. https://github.com/spring-projects/spring-framework