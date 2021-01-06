package com.tenx.logging.interceptor;

import com.tenx.logging.logger.OutboundLogger;
import com.tenx.logging.util.TemporalUtils;
import feign.Client;
import feign.Request;
import java.io.IOException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.BeanFactory;

@Aspect
public class FeignClientAspect {

  private final BeanFactory beanFactory;
  private final OutboundLogger outboundLogger;
  private final TemporalUtils temporalUtils;

  public FeignClientAspect(BeanFactory beanFactory, OutboundLogger outboundLogger, TemporalUtils temporalUtils) {
    this.beanFactory = beanFactory;
    this.outboundLogger = outboundLogger;
    this.temporalUtils = temporalUtils;
  }

  @Around("execution (* feign.Client.*(..)) && !within(is(FinalType))")
  public Object logFeignClientWasCalled(final ProceedingJoinPoint pjp) throws Throwable {
    Object bean = pjp.getTarget();
    Object wrappedBean = new FeignClientInterceptor(this.beanFactory, (Client) bean, outboundLogger, temporalUtils);
    return executeFeignClientInterceptor(wrappedBean, pjp);
  }

  Object executeFeignClientInterceptor(Object bean, ProceedingJoinPoint pjp) throws IOException {
    Object[] args = pjp.getArgs();
    Request request = (Request) args[0];
    Request.Options options = (Request.Options) args[1];
    return ((Client) bean).execute(request, options);
  }

}