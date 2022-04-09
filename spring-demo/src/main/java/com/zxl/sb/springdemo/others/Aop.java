package com.zxl.sb.springdemo.others;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Aop {
    protected final Log logger = LogFactory.getLog(this.getClass());

    public static void main(String[] args) {
        // 加载spring上下文
        // reader 读取@Component @Bean @Import等 + register(初始化)各类BeanFactoryPostProcessor和BeanPostProcessor,比如：ConfigurationClassPostProcessor\DefaultEventListenerFactory\AotuwiredAnnotationBeanPostFactory...
        // scanner 读取@ComponentScanner(basePackages="")

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
        //invokeBeanFactoryPostProcessors
        //finishBeanFactoryInitialization

        context.refresh();
        Car car = context.getBean("car", Car.class);
        System.out.println(car.getName());
    }

    /**
     *  知乎： https://www.zhihu.com/question/23641679
     *  AOP过程中主要的两个步骤  1.代理的创建   2.代理的调用 -- 根据拦截链顺序执行通知方法和目标对象方法
     *
     *  AbstractAotuProxyCreator createProxy() --> proxyFactory.getProxy(this.getProxyClassLoader())
     * 1.每个Bean都会被JDK或者Cglib代理。取决于是否有接口。目标对象如果是final类，也没有实现接口，就不能运用AOP。
     *   应用场景：@Configuration修饰类为例：该类对象方法A调用类内另一方法B，会走getIndex路由，所以会走动态代理类
     *
     *  目标对象中@EnableAspectJAutoProxy(proxyTargetClass = true)则即使有接口，也强制走CGLIB代理。
     *  目标对象中@EnableAspectJAutoProxy(exposeProxy = true)则可以oldProxy = AopContext.setCurrentProxy(proxy)
     *  应用场景：JDK代理中T类中方法A调用本类方法B时，是不会走动态代理的，但是设置exposeProxy = true后，可以在方法A中 (（T）AopContext.setCurrentProxy()).B()时可触发动态代理的增强。
     *
     *   Mapper接口是由Mybatis创建的动态代理，不是由Spring。
     *
     *
     * 2.每个Bean会有多个“方法拦截器”。注意：拦截器分为两层，外层由Spring内核控制流程；内层拦截器是用户设置，也就是AOP。
     * 3.当代理方法被调用时，先经过外层拦截器，外层拦截器根据方法的各种信息判断该方法应该执行哪些“内层拦截器”。内层拦截器的设计就是职责链的设计。
     *
     *
     *
     * JDK动态代理     代理类   Proxy
     * 		以IDao接口为例： 动态代理类为：$Proxy0
     * 		public final class $Proxy0 extend Proxy implements IDao {
     * 			private static Method m0;
     * 			private static Method m1;
     * 			private static Method m2;
     *
     * 			public $Proxy0(InvocationHandler var1) throws{
     * 				super(var1)
     *                        }
     *
     * 			public final boolean equals(Object var1) throws{
     * 				try{
     * 					return (Boolean)super.h.invoke(this,m1,new Object[]{var1});
     *                }catch(Throwable var4){
     * 					throw new ....
     *                }
     *            }* 		}
     *
     * CGLIB动态代理   代理类   getIndex(Signature var1)   AOPContext.getCurrentProxy.doXXX()
     *         以Dao类为例：  Dao$$EnhancerByCGLIB$$eaa57ed
     * 		public class Dao$$EnhancerByCGLIB$$eaa57ed{
     * 			public int getIndex(Singnature var1){
     * 				switch(var1.hashCode()){
     * 					case -2071771415;
     * 					if(var1.equals("getClass()Ljava/lang/class;")){
     * 						return CGLIB$select$1$Proxy;
     *                    }
     * 					break;
     * 					case -2055565910;
     * 					if(var1.equals("clone()Ljava/lang/class;")){
     * 						return CGLIB$finalize$2$Proxy;
     *                    }
     * 					break;
     * 					case -1826985398;
     * 					if(var1.equals("insert()V")){
     * 						return CGLIB$insert$0$Proxy;
     *                    }
     * 					break;
     *                }
     *            }
     *        }
     *
     *
     *
     * DefaultAopProxyFactory 源码
     * public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {
     *     public DefaultAopProxyFactory() {}
     *
     *     public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
     *         if (!config.isOptimize() && !config.isProxyTargetClass() && !this.hasNoUserSuppliedProxyInterfaces(config)) {
     *             return new JdkDynamicAopProxy(config);
     *         } else {
     *             Class<?> targetClass = config.getTargetClass();
     *             if (targetClass == null) {
     *                 throw new AopConfigException("TargetSource cannot determine target class: Either an interface or a target is required for proxy creation.");
     *             } else {
     *                 return (AopProxy)(!targetClass.isInterface() && !Proxy.isProxyClass(targetClass) ? new ObjenesisCglibAopProxy(config) : new JdkDynamicAopProxy(config));
     *             }
     *         }
     *     }
     *
     *     private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
     *         Class<?>[] ifcs = config.getProxiedInterfaces();
     *         return ifcs.length == 0 || ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0]);
     *     }
     * }
     *
     *
     * JdkDynamicAopProxy 源码
     * @Nullable
     *     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
     *         Object oldProxy = null;
     *         boolean setProxyContext = false;
     *         TargetSource targetSource = this.advised.targetSource;
     *         Object target = null;
     *
     *         Boolean var8;
     *         try {
     *             if (this.equalsDefined || !AopUtils.isEqualsMethod(method)) {
     *
     *                 if (this.advised.exposeProxy) {
     *                     oldProxy = AopContext.setCurrentProxy(proxy);
     *                     setProxyContext = true;
     *                 }
     *
     *                 target = targetSource.getTarget();
     *                 Class<?> targetClass = target != null ? target.getClass() : null;
     *                 List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
     *                 if (chain.isEmpty()) {
     *                     Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
     *                     retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
     *                 } else {
     *                     MethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
     *                     retVal = invocation.proceed();
     *                 }
     *
     *             }
     *
     *         return var8;
     *     }
     *
     *
     * 拦截链体现：
     * ReflectiveMethodInvocation 源码
     * 	@Nullable
     *     public Object proceed() throws Throwable {
     *         if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
     *             return this.invokeJoinpoint();
     *         } else {
     *             Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
     *             if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
     *                 InterceptorAndDynamicMethodMatcher dm = (InterceptorAndDynamicMethodMatcher)interceptorOrInterceptionAdvice;
     *                 Class<?> targetClass = this.targetClass != null ? this.targetClass : this.method.getDeclaringClass();
     *                 return dm.methodMatcher.matches(this.method, targetClass, this.arguments) ? dm.interceptor.invoke(this) : this.proceed();
     *             } else {
     *                 return ((MethodInterceptor)interceptorOrInterceptionAdvice).invoke(this);
     *             }
     *         }
     *     }
     *
     * ExposeInvocationInterceptor 源码
     * 	public Object invoke(MethodInvocation mi) throws Throwable {
     *         MethodInvocation oldInvocation = (MethodInvocation)invocation.get();
     *         invocation.set(mi);
     *
     *         Object var3;
     *         try {
     *             var3 = mi.proceed();
     *         } finally {
     *             invocation.set(oldInvocation);
     *         }
     *
     *         return var3;
     *     }
     *
     *
     *
     */

}
