package com.indigo.filemanager.common.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

public class HMACSHA1EncryptUtil {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	/**
	 * 使用 HMAC-SHA1 签名方法对data进行签名
	 * 
	 * @param data 被签名的字符串
	 * @param key  密钥
	 * @return 加密后的字符串
	 */
	public static String genHMAC(String data, String key) {
		byte[] result = null;
		try {
			// 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA1_ALGORITHM);
			// 生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			// 用给定密钥初始化 Mac 对象
			mac.init(signinKey);
			// 完成 Mac 操作
			byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			result = Base64.encodeBase64(rawHmac);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (InvalidKeyException e) {
			return null;
		}
		if (null != result) {
			return new String(result,StandardCharsets.UTF_8);
		} else {
			return null;
		}
	}

}
