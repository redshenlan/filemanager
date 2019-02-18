package com.indigo.filemanager.common.security.sign.aspect;

import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.indigo.filemanager.bus.domain.entity.AccessKey;
import com.indigo.filemanager.bus.service.AccessKeyManager;
import com.indigo.filemanager.common.security.sign.exception.CheckSignatureFailureException;
import com.indigo.filemanager.common.security.sign.exception.SignatureExceptionEnum;
import com.indigo.filemanager.common.util.HMACSHA1EncryptUtil;

/**
 * 
 * @author zhangqin
 *
 */
@Aspect
@Component
public class CheckSignatureAspect {
	
	private static final String HEADER_AUTHORIZATION = "Authorization";
	
	private static final String HEADER_DATE = "Date";
	
	private static final String ACCESSKEYSECRET_SPLITER = ":";
	
	private static final String ACCESSKEYID_PREFIX = "FS ";
	
	private static final int REQUESTAUTH_ARRAY_MIN_LENGTH = 2;
	
	@Resource
	private AccessKeyManager accessKeyManager;
	
	@Pointcut("@annotation(com.indigo.filemanager.common.security.sign.annotation.NeedCheckSignature)")
	public void checkSignaturePointcut() {
		
	}
	
	@Around("checkSignaturePointcut()")
    public Object execute(ProceedingJoinPoint pjp) throws Throwable {
		ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if(null == servletRequestAttributes) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.NO_HTTP_REQUEST);
		}
		HttpServletRequest request = servletRequestAttributes.getRequest();
		
		String requestAuthorization = request.getHeader(HEADER_AUTHORIZATION);
		if(StringUtils.isEmpty(requestAuthorization)) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.NO_SIGNATURE_INFO);
		}
		String[] requestAuth = requestAuthorization.split(ACCESSKEYSECRET_SPLITER);
		if(requestAuth.length < REQUESTAUTH_ARRAY_MIN_LENGTH) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.INCOMPLETE_SIGNATURE_INFO);
		}
		
		String accessKeyId = requestAuth[0].replace(ACCESSKEYID_PREFIX, "");
		// 根据AccessKeyId查询AccessKeySecret
		AccessKey accessKey = accessKeyManager.findByAccessKeyId(accessKeyId);
		if(null == accessKey) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.NO_MATCHED_ACCESSKEY);
		}
		String accessKeySecret = accessKey.getAccessKeySecret();
		
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
        } else {
        	throw new CheckSignatureFailureException(SignatureExceptionEnum.INVALID_HTTP_METHOD);
        }
        String requestUri = request.getRequestURI();
		String requestDate = request.getHeader(HEADER_DATE);
		
		String signature = HMACSHA1EncryptUtil.genHMAC(requestVerb + "\n" + requestUri + "\n" + requestDate + "\n", accessKeySecret);
		// 失败时抛出校验失败的异常
		if(!signature.equals(requestSignature)) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.SIGNATURE_CHECK_FAILURE);
		}
		
		return pjp.proceed();
	}
    		

}
