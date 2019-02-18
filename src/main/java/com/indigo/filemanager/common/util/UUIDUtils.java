package com.indigo.filemanager.common.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * @Description:ID工具类
 * @author: qiaoyuxi
 * @time: 2019年2月11日 下午2:41:10
 */
public class UUIDUtils {

	public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
	
	/**
	 * 生成文件编码
	 * @param suffix
	 * @return
	 */
	public static String getFileCode(String suffix){
		//生成时间戳yyyyMMddhhmmssSSSSS
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
		//生成6位随机数
		String sources = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
		Random rand = new Random();
		StringBuffer flag = new StringBuffer();
		for (int j = 0; j < 6; j++) {
			flag.append(sources.charAt(rand.nextInt(sources.length())) + "");
		}
		//返回文件代码
		return suffix.toUpperCase()+dateTime+flag.toString();
	}
	
}
