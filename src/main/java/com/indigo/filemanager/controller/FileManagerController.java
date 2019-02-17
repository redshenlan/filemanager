package com.indigo.filemanager.controller;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.indigo.filemanager.bus.service.FileManager;
import com.indigo.filemanager.common.Constants;
import com.indigo.filemanager.common.ServerResponse;
import com.indigo.filemanager.common.UUIDUtils;
import com.indigo.filemanager.persistence.vo.FileInfo;

/**
 * @Description:文件管理
 * @author: qiaoyuxi
 * @time: 2019年2月11日 上午10:57:17
 */
@RestController
public class FileManagerController {

	@Autowired
    private FileManager fileManager;
	
	/**
	 * 上传文件
	 * @param file
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/files")
	public ServerResponse upload(@RequestParam("file") MultipartFile file,@RequestParam("userCode") String userCode) {
		try {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setFile(file.getInputStream());
			fileInfo.setFileKey(UUIDUtils.getUUID());
			String fileSuffix = file.getOriginalFilename().substring(
					file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
			fileInfo.setFileSuffix(fileSuffix);
			fileInfo.setFileSize(file.getSize());
			fileInfo.setFileName(file.getOriginalFilename());
			fileManager.uploadFile(fileInfo,userCode);
			Map<String,Object> dataMap = new HashMap<String,Object>();
			dataMap.put("filekey", fileInfo.getFileKey());
			return ServerResponse.success(dataMap);
		} catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.failure(Constants.Error.DEFAULT.eval(), e.getMessage());
		}
	}
	
	/**
	 * 下载文件
	 * @param filekey
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/files/{filekey}")
	public ResponseEntity<byte[]> download(@PathVariable("filekey") String filekey
			,@RequestParam("userCode") String userCode) throws Exception {
		FileInfo fileInfo = fileManager.downloadFile(filekey,userCode);
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  
	    headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileInfo.getFileName(),"UTF-8")); 
	    ByteArrayOutputStream baos = (ByteArrayOutputStream)fileInfo.getDownFile();
	    return new ResponseEntity<byte[]>(baos.toByteArray(),headers, HttpStatus.CREATED);
	}
	
	/**
	 * 更新文件
	 * @param file
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/files/{filekey}")
	public ServerResponse update(@PathVariable("filekey") String filekey
			,@RequestParam("file") MultipartFile file,@RequestParam("userCode") String userCode) {
		try {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setFile(file.getInputStream());
			fileInfo.setFileKey(filekey);
			String fileSuffix = file.getOriginalFilename().substring(
					file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
			fileInfo.setFileSuffix(fileSuffix);
			fileInfo.setFileSize(file.getSize());
			fileInfo.setFileName(file.getOriginalFilename());
			fileManager.updateFile(fileInfo,userCode);
			return ServerResponse.success();
		} catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.failure(Constants.Error.DEFAULT.eval(), e.getMessage());
		}
	}
	
}
