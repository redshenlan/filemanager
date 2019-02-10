package com.indigo.filemanager.common.security.sign.aspect;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.indigo.filemanager.common.security.sign.exception.CheckSignatureFailureException;
import com.indigo.filemanager.common.util.HMACSHA1EncryptUtil;

@Aspect
@Component
public class CheckSignatureAspect {
	
	private static final String Header_Authorization = "Authorization";
	
	private static final String Header_Date = "Date";
	
	private static final String AccessKeySecret_Spliter = ":";
	
	private static final String AccessKeyId_Prefix = "FS ";
	
	@Pointcut("@annotation(com.indigo.filemanager.common.security.sign.annotation.NeedCheckSignature)")
	public void checkSignaturePointcut() {
		
	}
	
//	@Around("excudeService()")
    public Object execute(ProceedingJoinPoint pjp) throws Throwable {
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		String requestAuthorization = request.getHeader(Header_Authorization);
		String[] requestAuth = requestAuthorization.split(AccessKeySecret_Spliter);
		
		String accessKeyId = requestAuth[0].replace(AccessKeyId_Prefix, "");
		// 根据AccessKeyId查询AccessKeySecret
		String accessKeySecret = "";
		
		String requestSignature = requestAuth[1];
		
		MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        String requestVerb = "";
        if (method.isAnnotationPresent(PutMapping.class)) {
        	requestVerb = "PUT";
        } else if (method.isAnnotationPresent(PostMapping.class)) {
        	requestVerb = "POST";
        } else if (method.isAnnotationPresent(GetMapping.class)) {
        	requestVerb = "GET";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
        	requestVerb = "DELETE";
        }
        String requestUri = request.getRequestURI();
		String requestDate = request.getHeader(Header_Date);
		
		String signature = HMACSHA1EncryptUtil.genHMAC(requestVerb + "\n" + requestUri + "\n" + requestDate + "\n", accessKeySecret);
		// 失败时抛出校验失败的异常
		if(!signature.equals(requestSignature)) {
			throw new CheckSignatureFailureException("");
		}
		
		return pjp.proceed();
	}
    		

}
