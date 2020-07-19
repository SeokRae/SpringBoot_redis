## DefaultSerializer requires a Serializable payload but received an object of type
- Serializable 오류 발생

- 해결책
    - 해당 객체에 Serializable 구현

```java
@Getter
@NoArgsConstructor
public class User implements Serializable {

```

```shell script
2020-07-18 18:17:41.430 ERROR 2399 --- [nio-8080-exec-3] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.data.redis.serializer.SerializationException: Cannot serialize; nested exception is org.springframework.core.serializer.support.SerializationFailedException: Failed to serialize object using DefaultSerializer; nested exception is java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [com.sample.domain.User]] with root cause

java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [com.sample.domain.User]
	at org.springframework.core.serializer.DefaultSerializer.serialize(DefaultSerializer.java:43) ~[spring-core-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.core.serializer.support.SerializingConverter.convert(SerializingConverter.java:63) ~[spring-core-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.core.serializer.support.SerializingConverter.convert(SerializingConverter.java:35) ~[spring-core-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.data.redis.serializer.JdkSerializationRedisSerializer.serialize(JdkSerializationRedisSerializer.java:94) ~[spring-data-redis-2.1.11.RELEASE.jar:2.1.11.RELEASE]
	at org.springframework.data.redis.core.AbstractOperations.rawHashValue(AbstractOperations.java:185) ~[spring-data-redis-2.1.11.RELEASE.jar:2.1.11.RELEASE]
	at org.springframework.data.redis.core.DefaultHashOperations.put(DefaultHashOperations.java:189) ~[spring-data-redis-2.1.11.RELEASE.jar:2.1.11.RELEASE]
	at com.sample.repository.UserRepositoryImpl.save(UserRepositoryImpl.java:24) ~[main/:na]
	at com.sample.repository.UserRepositoryImpl$$FastClassBySpringCGLIB$$299dd182.invoke(<generated>) ~[main/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) ~[spring-core-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:750) ~[spring-aop-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) ~[spring-aop-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:139) ~[spring-tx-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) ~[spring-aop-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:689) ~[spring-aop-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at com.sample.repository.UserRepositoryImpl$$EnhancerBySpringCGLIB$$7ddf8747.save(<generated>) ~[main/:na]
	at com.sample.controller.UserController.add(UserController.java:22) ~[main/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_251]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_251]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_251]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_251]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:105) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:893) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:798) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:634) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883) ~[spring-webmvc-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:741) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53) ~[tomcat-embed-websocket-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:94) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.1.10.RELEASE.jar:5.1.10.RELEASE]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202) ~[tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:526) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:860) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1589) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_251]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_251]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-9.0.26.jar:9.0.26]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_251]


```