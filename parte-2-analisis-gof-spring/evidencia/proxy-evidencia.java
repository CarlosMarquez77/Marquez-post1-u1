// Fuente: https://github.com/spring-projects/spring-framework/blob/main/spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java
// Módulo: spring-aop | Paquete: org.springframework.aop.framework
// Patrón: Proxy (Estructural)

final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {
    /** use serialVersionUID from Spring 1.2 for interoperability. */
    private static final long serialVersionUID = 5531744639992436476L;
    private static final String COROUTINES_FLOW_CLASS_NAME = "kotlinx.coroutines.flow.Flow";
    private static final boolean COROUTINES_REACTOR_PRESENT = ClassUtils.isPresent(
            "kotlinx.coroutines.reactor.MonoKt", JdkDynamicAopProxy.class.getClassLoader());
    /** We use a static Log to avoid serialization issues. */
    private static final Log logger = LogFactory.getLog(JdkDynamicAopProxy.class);
    /** Config used to configure this proxy. */
    private final AdvisedSupport advised;
    /** Cached in {@link AdvisedSupport#proxyMetadataCache}. */
    private transient ProxiedInterfacesCache cache;

public @Nullable Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object oldProxy = null;
        boolean setProxyContext = false;
        TargetSource targetSource = this.advised.targetSource;
        Object target = null;