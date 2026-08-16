// Fuente: https://github.com/spring-projects/spring-framework/blob/main/spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java
// Módulo: spring-beans | Paquete: org.springframework.beans.factory.support
// Patrón: Singleton (Creacional)

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

