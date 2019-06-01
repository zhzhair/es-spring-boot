package com.example.es.config.aspect;

import com.alibaba.fastjson.JSONObject;
import com.example.es.config.aspect.annotation.LogForController;
import com.example.es.config.aspect.annotation.LogForTimeConsumer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class LogAspect {

    @Pointcut(value = "execution(* com.example.es.*.controller.*.*(..))")
    public void logPointCut() {

    }

    @Pointcut(value = "execution(* com.example.es.*.*.*.*(..))")
    public void logPointCutOfTime() {

    }

    /**
     * 记录controller日志环绕通知
     */
    @Around(value = "logPointCut() && @annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(controllerLog)", argNames = "pjp,controllerLog")
    public Object autoLogRecord(ProceedingJoinPoint pjp, LogForController controllerLog) throws Throwable {
//        获取request
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes ra = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = ra.getRequest();
//        http请求的方法
        String method = request.getMethod();
//        请求路径
        String servletPath = request.getServletPath();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
//        获取参数名称列表
        String[] parameterNames = signature.getParameterNames();
//        获取方法名
        String name = signature.getName();
//        切入的类
        Class declaringType = signature.getDeclaringType();
//        日志对象
        Logger logger = LoggerFactory.getLogger(declaringType);
//        获取参数列表
        Object[] args = pjp.getArgs();
        Object[] newArr = new Object[args.length+2];
        StringBuilder var1 = new StringBuilder("前端调用方法开始----"+ name + "---->：#{\"URL地址\":{}, \"HTTP方法\":{}，参数：");
        if(args.length != 0) {
            System.arraycopy(args,0,newArr,2, args.length);
            for (String s : parameterNames) {
                var1.append(", \"").append(s).append("\":{}");
            }
        }
        var1.append("}");
        newArr[0] = servletPath;
        newArr[1] = method;
//          记录日志
        logger.info(var1.toString(),newArr);
        Object proceed = pjp.proceed();
        logger.info("前端调用方法结束----"+ name +"---->：返回值: {}",JSONObject.toJSONString(proceed));
        return proceed;
    }

    /**
     * 记录定时任务日志环绕通知
     */
    @Around(value = "logPointCutOfTime() && @annotation(timeLog)", argNames = "pjp,timeLog")
    public Object autoLogRecordOfTime(ProceedingJoinPoint pjp, LogForTimeConsumer timeLog) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
//        获取方法名
        String name = signature.getName();
//        切入的类
        Class declaringType = signature.getDeclaringType();
//        日志对象
        Logger logger = LoggerFactory.getLogger(declaringType);
        StopWatch sw = new StopWatch();
        sw.start(name);
        Object proceed = pjp.proceed();
        sw.stop();
//        logger.info("定时任务---"+name+"---结束---->，统计信息:{}",sw.prettyPrint());
        logger.info("定时任务---"+ name +"---结束---->，耗时:{}",sw.getTotalTimeMillis()/1000 + "秒");
        return proceed;
    }
}
