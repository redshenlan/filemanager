package com.indigo.filemanager.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.indigo.filemanager.bus.domain.entity.User;
import com.indigo.filemanager.bus.exception.FileOperateFailureException;
import com.indigo.filemanager.bus.service.FileManager;
import com.indigo.filemanager.bus.service.FileTransferService;
import com.indigo.filemanager.common.ServerResponse;
import com.indigo.filemanager.common.persistence.vo.FileInfo;
import com.indigo.filemanager.common.security.sign.annotation.CurrentUser;
import com.indigo.filemanager.common.security.sign.annotation.NeedCheckSignature;
import com.indigo.filemanager.common.util.UUIDUtils;

/**
 * @Description:文件管理
 * @author: qiaoyuxi
 * @time: 2019年2月11日 上午10:57:17
 */
@RestController
@Slf4j
public class FileManagerController {

	@Autowired
    private FileManager fileManager;
	
	@Autowired
    private FileTransferService fileTransferService;
	
	/**
	 * 上传文件
	 * @param user
	 * @param filename
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PutMapping(value = "/files/{filename}")
	@NeedCheckSignature
	public ServerResponse upload(@CurrentUser User user,@PathVariable("filename") String filename
			,@RequestParam("file") MultipartFile file) throws IOException {
		InputStream saveInputStream = file.getInputStream();
		String fileSuffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
		//保存上传文件
		FileInfo saveFileInfo = new FileInfo();
		saveFileInfo.setFile(saveInputStream);
		String uuid = UUIDUtils.getUUID();
		saveFileInfo.setFileKey(uuid);
		saveFileInfo.setFileSuffix(fileSuffix);
		saveFileInfo.setFileSize(file.getSize());
		saveFileInfo.setFileName(filename);
		fileManager.uploadFile(saveFileInfo,user);
		//转换文件并保存,转换失败不影响返回结果
		try {
			if (fileTransferService.canTransfer(fileSuffix)) {
				InputStream transferInputStream = file.getInputStream();
				FileInfo transferFileInfo = new FileInfo();
				transferFileInfo.setFile(transferInputStream);
				transferFileInfo.setFileKey(uuid);
				transferFileInfo.setFileSuffix(fileSuffix);
				fileManager.transferFile(transferFileInfo);
			}
		} catch (FileOperateFailureException ex) {
			log.error("文件转换失败,code:" + ex.getCode() + ",msg:" + ex.getMsg());
		} catch (Exception ex) {
			log.error("文件转换失败,msg:" + ex.getMessage());
		}
		//返回结果
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("filekey", uuid);
		return ServerResponse.success(dataMap);
	}
	
	/**
	 * 下载文件
	 * @param user
	 * @param filekey
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/files/{filekey}")
	@NeedCheckSignature
	public ResponseEntity<byte[]> download(@CurrentUser User user,@PathVariable("filekey") String filekey) throws Exception {
		FileInfo fileInfo = fileManager.downloadFile(filekey,user);
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileInfo.getFileName(),"UTF-8")); 
	    ByteArrayOutputStream baos = (ByteArrayOutputStream)fileInfo.getDownFile();
	    return new ResponseEntity<byte[]>(baos.toByteArray(),headers, HttpStatus.CREATED);
	}
	
	/**
	 * 删除文件
	 * @param user
	 * @param filekey
	 * @return
	 */
	@DeleteMapping(value = "/files/{filekey}")
	@NeedCheckSignature
	public ServerResponse delete(@CurrentUser User user,@PathVariable("filekey") String filekey) {
		fileManager.deleteFile(filekey, user);
		return ServerResponse.success();
	}
	
	/**
	 * 更新文件(仅更新文件名称)
	 * @param user
	 * @param filekey
	 * @param filename
	 * @return
	 */
	@PostMapping(value = "/files/{filekey}/{filename}")
	@NeedCheckSignature
	public ServerResponse update(@CurrentUser User user,@PathVariable("filekey") String filekey
			,@PathVariable("filename") String filename) {
		fileManager.updateFile(filekey, filename, user);
		return ServerResponse.success();
	}
	
}
