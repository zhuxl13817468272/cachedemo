package com.zxl.sb.springdemo.others;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.security.AccessController;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Demo {
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
     *
     *  Spring解决循环依赖 ：https://zhuanlan.zhihu.com/p/73570365
     *  一文告诉你Spring是如何利用“三级缓存“巧妙解决Bean的循环依赖问题的【享学Spring】 https://blog.csdn.net/f641385712/article/details/92801300
     *
     *      完整Spring创建Bean流程
     *          AbstractBeanFctory 源码                   AbstractAutowireCapableBeanFactory  源码                                         DefaultSingletonBeanRegistry 源码
     *      getBean() --> doGetBean(getSingleton()) --> createBean() --> doCreateBean(instance()之后放入三级缓存addSingletonFactory()) -- > registerSingleton(加入一级缓存addSingleton()) -->返回Bean
     *
     *     1. A依次执行doGetBean（查询缓存）, 再接着doCreateBean创建实例（实例化完成后放入三级缓存）、执行populateBean方法装配属性，但是发现有一个属性是B的对象。
     *     2. 因此再创建B的实例，依次执行doGetBean（查询缓存）、doCreateBean创建实例（实例化完成后放入三级缓存），在执行populateBean方法装配属性时发现有一个属性是A对象。
     *        因此再创建A的实例，但是执行到doGetBean（查询缓存）的getSingleton时，从三级缓存中查询到了A的实例(纯净Bean -- 早期引用,未完成属性装配)，此时直接返回A，
     *        那么B就完成了属性装配，此时B是一个成熟的Bean(完整的对象)放入到一级缓存singletonObjects中。
     *     3. B创建完成了，则A继续完成自己Bean的属性装配，创建完成后也放入了一级缓存singletonObjects中。
     *
     *
     *
     *  InitializingBean
     *     org.springframework.beans.factory包下有一个接口是InitializingBean 只有一个方法：void afterPropertiesSet() throws Exception;
     *     默认：这个方法将在所有的属性赋值后调用，但是会在init前调用。
     *     用法一：但是主要的是如果是延迟加载的话，则马上执行。所以可以在类上加上注解：
     *              import org.springframework.context.annotation.Lazy;
     *              @Lazy(false)
     *           这样spring容器初始化时，类实现InitializingBean接口，重写的afterPropertiesSet()就会被调用。
     *     用法二：
     *           public class InitializeFramework implements InitializingBean {
     *              @Override
     *              public void afterPropertiesSet() throws Exception {
     *                  try {
     *
     *                  } catch (Exception e) {
     *
     *                  }
     *               }
     *            }
     *      执行时机：Spring的 BeanFactory 实现将在上下文中检测到InitializingBean类型的实例，并在设置了对象的所有属性后，调用afterPropertiesSet()方法。
     *               您也可以通过由InitializeFramework实现构造ApplicationContext bean来进行测试。
     *                  @Configuration
     *                  public class MyConfiguration {
     *                      @Bean
     *                      public InitializeFramework initializeFramework() {
     *                          return new InitializeFramework();
     *                      }
     *                  }
     *
     *    init-method
     *         用法一：
     *              @Configuration
     *              @ComponentScan("springTest2")
     *              public class ConfigTest {
     *                  @Bean(initMethod = "init", destroyMethod = "destroy")
     *                  Test1 test1() {
     *                      return new Test1();
     *                  }
     *              }
     *         用法二：
     *              public class Test2 {
     *                  @PostConstruct
     *                  public void init() {
     *                      System.out.println("this is init method2");
     *                  }
     *                  public Test2() {
     *                      super();
     *                      System.out.println("构造函数2");
     *                  }
     *                  @PreDestroy
     *                  public void destroy() {
     *                      System.out.println("this is destroy method2");
     *                  }
     *              }
     *        用法三：xml配置的方式
     *
     * 总结： 1.BeanPostProcessor的执行时机
     *          populateBean(beanName, mbd, instanceWrapper)
     *          initializeBean{
     *              applyBeanPostProcessorsBeforeInitialization()
     *              invokeInitMethods{
     *                  isInitializingBean.afterPropertiesSet()
     *                  自定义的init方法inti-method
     *              }
     *              applyBeanPostProcessorsAfterInitialization()方法
     *          }
     *        2. BeanDefinition接口
     *           RootBeanDefinition:代表一个xml\java config来的
     *          @Configuration注解类 AnnotatedGenericBeanDefinition类
     *          @Component\@Resposity\@Service\@Controller注解类 ScannedGenericBeanDefinition类
     *
     *          操作：动作也分两种，一种是针对自身的操作（自己提供给外部的方法，来操作其本身属性或方法）
     *                             另一种是外部对BeanDefinition的操作
     *                BeanDefinitionRegister接口：具有增、查、删BeanDefinition的能力。
     *                      void registerBeanDefinition(String,BeanDefinition)
     *                      void removeBeanDefinition(String)
     *                      BeanDifinition getBeanDefinition(String)
     *                BeanDefinitionReader接口
     *                      AnnotatedBeanDefinitionReader类 对带有注解的BeanDefinition进行注册
     *                      ClassPathBeanDefinitionScanner类 可以扫描到@Component\@Repository\@Service\@Controller的BeanDefinition注册到容器中
     *        3. BeanWrapper 可以看做是一个从BeanDefinition到Bean过程中间的产物。是Spring框架中重要的组件类，它就是相当于一个代理类，Spring委托BeanWrapper完成Bean属性的填充工作。
     *        4. Bean 是我们需要的对象
     *                  SingletonBeanRegister接口 提供了对Bean的注册、获取、存在性判断等功能
     *                  InstantiationStrategy提供Bean实例化的策略接口
     *                  InitializingBean 对于实现InitializingBean的Bean,在所有的Bean属性被设置之后,它将执行afterPropertiesSet()。
     *                  DisposableBean 对于实现了DisposableBean的Bean,在Spring容器释放该bean之后,它将运行destroy()。
     *                  FactoryBean 生成Bean的Bean
     *
     *
     *
     *
     *
     */

    /**
     *  第一部分：最初查询缓存。从源码可以得知，doGetBean最初是查询缓存，一二三级缓存全部查询，如果三级缓存存在则将Bean早期引用存放在二级缓存中并移除三级缓存。（升级为二级缓存）
     *  AbstractBeanFctory 源码
     */
    protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly) throws BeansException {
        String beanName = this.transformedBeanName(name);
        // 查询缓存
        Object sharedInstance = this.getSingleton(beanName,true);
        Object bean;
        //缓存中存在并且args是null
        if (sharedInstance != null && args == null) {
            //.......省略部分代码

            //直接获取Bean实例
            bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, (RootBeanDefinition)null);
        }

        //.......省略部分代码
        return null;

    }


    /**
     *  DefaultSingletonBeanRegistry 源码
     */
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16));

    /**一级缓存，用于存放完全初始化好的 bean，从该缓存中取出的 bean 可以直接使用*/
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /**三级缓存 存放 bean 工厂对象，用于解决循环依赖*/
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    /**二级缓存 存放原始的 bean 对象（尚未填充属性），用于解决循环依赖*/
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    @Nullable
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        // 一级缓存里面为空，且处于正在创建中标识
        if (singletonObject == null && this.isSingletonCurrentlyInCreation(beanName)) {
            Map var4 = this.singletonObjects;

            synchronized(this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                // 二级缓存里面为空，且允许创建早期引用（二级缓存中添加）
                if (singletonObject == null && allowEarlyReference) {

                    ObjectFactory<?> singletonFactory = (ObjectFactory)this.singletonFactories.get(beanName);
                    // 三级缓存为什么一定不为空？因为三级缓存存放的位置（实例化后就存放）
                    if (singletonFactory != null) {
                        // 调用函数式接口ObjectFactory的getObject()方法
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }

        return singletonObject;
    }



    private Object getObjectForBeanInstance(Object sharedInstance, String name, String beanName, RootBeanDefinition rootBeanDefinition) {
    }

    private String transformedBeanName(String name) {
    }


    /**
     *  第二部分：纯净Bean的处理。从源码得知，Bean在实例化完成之后会直接将未装配的Bean工厂存放在三级缓存中，并且移除二级缓存
     *  AbstractAutowireCapableBeanFactory  源码
     */
    private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>(256);
    private boolean allowCircularReferences = true;
    private final Set<String> registeredSingletons = new LinkedHashSet(256);


    protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = (BeanWrapper)this.factoryBeanInstanceCache.remove(beanName);
        }

        if (instanceWrapper == null) {
            // 实例化bean 从RootBeanDefinition通过反射后包装到BeanWrapper中
            instanceWrapper = this.createBeanInstance(beanName, mbd, args);
        }

        Object bean = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();

        boolean earlySingletonExposure = mbd.isSingleton() && this.allowCircularReferences && this.isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            // 添加到三级缓存中
            this.addSingletonFactory(beanName, () -> {
                return this.getEarlyBeanReference(beanName, mbd, bean);
            });
        }

        Object exposedObject = bean;

        try {
            // 属性装配，属性赋值时，如果有发现属性调用了另外一个bean,则调用getBean方法
            this.populateBean(beanName, mbd, instanceWrapper);
            // 初始化Bean，调用InitializingBean、init-method方法等操作
            exposedObject = this.initializeBean(beanName, exposedObject, mbd);
        } catch (Throwable var18) {
            if (var18 instanceof BeanCreationException && beanName.equals(((BeanCreationException)var18).getBeanName())) {
                throw (BeanCreationException)var18;
            }

            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", var18);
        }

        if (earlySingletonExposure) {
            Object earlySingletonReference = this.getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                } else if (!this.allowRawInjectionDespiteWrapping && this.hasDependentBean(beanName)) {
                    String[] dependentBeans = this.getDependentBeans(beanName);
                    Set<String> actualDependentBeans = new LinkedHashSet(dependentBeans.length);
                    String[] var12 = dependentBeans;
                    int var13 = dependentBeans.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        String dependentBean = var12[var14];
                        if (!this.removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }

                    if (!actualDependentBeans.isEmpty()) {
                        throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
                    }
                }
            }
        }

        try {
            this.registerDisposableBeanIfNecessary(beanName, bean, mbd);
            return exposedObject;
        } catch (BeanDefinitionValidationException var16) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", var16);
        }
    }


    /**
     *  DefaultSingletonBeanRegistry 源码
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        Map var3 = this.singletonObjects;
        synchronized(this.singletonObjects) {
            // 一级缓存不存在
            if (!this.singletonObjects.containsKey(beanName)) {
                // 放入三级缓存
                this.singletonFactories.put(beanName, singletonFactory);
                // 删除二级缓存
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }

        }
    }


    /**
     *  第三部分：Bean初始化之后，成熟的Bean处理。成熟的Bean添加到一级缓存，移除二三级缓存
     *  DefaultSingletonBeanRegistry 源码
     */
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {

        synchronized(this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            } else {
                //doCreateBean之后才调用，实例化，属性赋值完成的Bean装入一级缓存，可以直接使用的Bean
                this.addSingleton(beanName, singletonObject);
            }
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        Map var3 = this.singletonObjects;
        synchronized(this.singletonObjects) {
            // 添加到一级缓存
            this.singletonObjects.put(beanName, singletonObject);
            // 移除三级缓存
            this.singletonFactories.remove(beanName);
            // 移除二级缓存
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }



    /**
     *  mbd --> bw BeanWrapper
     * @param beanName
     * @param mbd
     * @param args
     * @return
     */
    private BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
    }

    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        Object exposedObject = bean;
        if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            Iterator var5 = this.getBeanPostProcessors().iterator();

            while(var5.hasNext()) {
                BeanPostProcessor bp = (BeanPostProcessor)var5.next();
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                    exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
                }
            }
        }

        return exposedObject;
    }

    protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
        if (bw == null) {
            if (mbd.hasPropertyValues()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
            }
        } else {
            if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
                Iterator var4 = this.getBeanPostProcessors().iterator();

                while(var4.hasNext()) {
                    BeanPostProcessor bp = (BeanPostProcessor)var4.next();
                    if (bp instanceof InstantiationAwareBeanPostProcessor) {
                        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                        if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                            return;
                        }
                    }
                }
            }

            PropertyValues pvs = mbd.hasPropertyValues() ? mbd.getPropertyValues() : null;
            int resolvedAutowireMode = mbd.getResolvedAutowireMode();
            if (resolvedAutowireMode == 1 || resolvedAutowireMode == 2) {
                MutablePropertyValues newPvs = new MutablePropertyValues((PropertyValues)pvs);
                if (resolvedAutowireMode == 1) {
                    this.autowireByName(beanName, mbd, bw, newPvs);
                }

                if (resolvedAutowireMode == 2) {
                    this.autowireByType(beanName, mbd, bw, newPvs);
                }

                pvs = newPvs;
            }

            boolean hasInstAwareBpps = this.hasInstantiationAwareBeanPostProcessors();
            boolean needsDepCheck = mbd.getDependencyCheck() != 0;
            PropertyDescriptor[] filteredPds = null;
            if (hasInstAwareBpps) {
                if (pvs == null) {
                    pvs = mbd.getPropertyValues();
                }

                Iterator var9 = this.getBeanPostProcessors().iterator();

                while(var9.hasNext()) {
                    BeanPostProcessor bp = (BeanPostProcessor)var9.next();
                    if (bp instanceof InstantiationAwareBeanPostProcessor) {
                        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                        PropertyValues pvsToUse = ibp.postProcessProperties((PropertyValues)pvs, bw.getWrappedInstance(), beanName);
                        if (pvsToUse == null) {
                            if (filteredPds == null) {
                                filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                            }

                            pvsToUse = ibp.postProcessPropertyValues((PropertyValues)pvs, filteredPds, bw.getWrappedInstance(), beanName);
                            if (pvsToUse == null) {
                                return;
                            }
                        }

                        pvs = pvsToUse;
                    }
                }
            }

            if (needsDepCheck) {
                if (filteredPds == null) {
                    filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                }

                this.checkDependencies(beanName, mbd, filteredPds, (PropertyValues)pvs);
            }

            if (pvs != null) {
                this.applyPropertyValues(beanName, mbd, bw, (PropertyValues)pvs);
            }

        }
    }

    protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(() -> {
                this.invokeAwareMethods(beanName, bean);
                return null;
            }, this.getAccessControlContext());
        } else {
            this.invokeAwareMethods(beanName, bean);
        }

        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        }

        try {
            this.invokeInitMethods(beanName, wrappedBean, mbd);
        } catch (Throwable var6) {
            throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", var6);
        }

        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }

        return wrappedBean;
    }




}
