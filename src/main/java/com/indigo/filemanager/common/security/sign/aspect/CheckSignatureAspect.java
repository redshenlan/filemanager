package com.indigo.filemanager.common.security.sign.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.indigo.filemanager.bus.domain.entity.User;
import com.indigo.filemanager.bus.service.AccessKeyManager;
import com.indigo.filemanager.common.security.sign.annotation.CurrentUser;
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
	
	private static final String HEADER_EXPIRE = "Expire";
	
	private static final String ACCESSKEYSECRET_SPLITER = ":";
	
	private static final String ACCESSKEYID_PREFIX = "FS ";
	
	private static final int REQUESTAUTH_ARRAY_MIN_LENGTH = 2;
	
	private static final List<String> ALLOW_REQUEST_METHOD  = Arrays.asList("PUT", "POST", "GET", "DELETE");
	
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
		
		String requestExpire = request.getHeader(HEADER_EXPIRE);
		if(StringUtils.isEmpty(requestExpire)) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.NO_EXPIRE_INFO);
		}
		long expire = Long.parseLong(requestExpire);
		long current = System.currentTimeMillis();
		// 如果当前时间大于过期时间，说明签名已过期
		if(current > expire) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.SIGNATURE_HAS_EXPIRED);
		}
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
		User user = accessKeyManager.findByAccessKeyId(accessKeyId);
		if(null == user) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.NO_MATCHED_ACCESSKEY);
		}
		String accessKeySecret = user.getAccessKeySecret();
		
		String requestSignature = requestAuth[1];
		
		MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        String requestVerb = request.getMethod();
        if (!ALLOW_REQUEST_METHOD.contains(requestVerb)) {
        	throw new CheckSignatureFailureException(SignatureExceptionEnum.INVALID_HTTP_METHOD);
        }
        String requestUri = request.getRequestURI();
		
		String signature = HMACSHA1EncryptUtil.genHMAC(requestVerb + "\n" + requestUri + "\n" + requestExpire + "\n", accessKeySecret);
		// 失败时抛出校验失败的异常
		if(!requestSignature.equals(signature)) {
			throw new CheckSignatureFailureException(SignatureExceptionEnum.SIGNATURE_CHECK_FAILURE);
		}
		
		boolean needModifyParameter = false;
        Parameter[] parameters = method.getParameters();
        Object[] pjpArgs = pjp.getArgs();
        for(int i = 0; i < parameters.length; i++) {
        	if(parameters[i].isAnnotationPresent(CurrentUser.class)) {
        		needModifyParameter = true;
        		pjpArgs[i] = user;
        	}
        }
		
		if(needModifyParameter) {
			return pjp.proceed(pjpArgs);
		} else {
			return pjp.proceed();
		}
	}
    		

}
