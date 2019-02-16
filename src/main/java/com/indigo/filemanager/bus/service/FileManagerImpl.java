package com.indigo.filemanager.bus.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indigo.filemanager.bus.domain.FileOperateRecordRepository;
import com.indigo.filemanager.bus.domain.FileRecordRepository;
import com.indigo.filemanager.bus.domain.MenuRepository;
import com.indigo.filemanager.bus.domain.UserRepository;
import com.indigo.filemanager.bus.domain.entity.FileOperateRecord;
import com.indigo.filemanager.bus.domain.entity.FileRecord;
import com.indigo.filemanager.bus.domain.entity.Menu;
import com.indigo.filemanager.bus.domain.entity.User;
import com.indigo.filemanager.common.Constants;
import com.indigo.filemanager.common.UUIDUtils;
import com.indigo.filemanager.persistence.FileUtils;
import com.indigo.filemanager.persistence.vo.FileInfo;
import com.indigo.filemanager.persistence.vo.SaveFileResult;
import com.indigo.filemanager.service.FileTransferService;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月11日 上午11:02:00
 */
@Service
public class FileManagerImpl implements FileManager{

	@Autowired
    private FileTransferService fileTransferService;
	@Autowired
    private MenuRepository menuRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private FileRecordRepository fileRecordRepository;
	@Autowired
    private FileOperateRecordRepository fileOperateRecordRepository;
	@Autowired
	private FileUtils fileUtils;
	
	/**
	 * 文件上传
	 * @param fileInfo
	 * @throws Exception 
	 */
	public void uploadFile(FileInfo fileInfo) throws Exception{
		ByteArrayOutputStream pdfOs = null;
		ByteArrayInputStream pdfIs = null;
		try{
			//转换pdf文件并保存
			if(false){//??哪些可以转换pdf
				pdfOs = (ByteArrayOutputStream)fileTransferService.transferPdf(fileInfo.getFile(),
						fileInfo.getFileSuffix(), "pdf");
				pdfIs = new ByteArrayInputStream(pdfOs.toByteArray());
				//保存pdf文件
				FileInfo pdfFileInfo = new FileInfo();
				pdfFileInfo.setFile(pdfIs);
				pdfFileInfo.setFileKey(fileInfo.getFileKey());
				pdfFileInfo.setFileSuffix("pdf");
				fileUtils.saveFile(pdfFileInfo);
			}
			//保存原文件
			SaveFileResult result = fileUtils.saveFile(fileInfo);
			if(!result.isResult()){
				throw new RuntimeException("文件持久化失败");
			}
			//保存关系数据库信息
			//校验用户？？
			User user = userRepository.findByUserCodeAndValid(fileInfo.getUserCode(),"Y");
			if(user!=null){
				//校验二级目录（根目录在创建业务系统时，自动创建）
				Menu menu = menuRepository.findByMenuNameAndBusinessSystemCodeAndParentIdNot(fileInfo.getFileSuffix(),
						user.getBusinessSystemCode(),Constants.MENU_ROOT_ID);
				if(menu==null){
					//创建二级目录，目录名称为文件后缀名
					menu = new Menu();
					menu.setBusinessSystemCode(user.getBusinessSystemCode());
					menu.setMenuName(fileInfo.getFileSuffix());
					Menu firstMenu = menuRepository.findByBusinessSystemCodeAndParentId(user.getBusinessSystemCode(),
							Constants.MENU_ROOT_ID);
					menu.setParentId(firstMenu.getId());
					menuRepository.save(menu);
				}
				//保存文件
				FileRecord fileRecord = new FileRecord();
				fileRecord.setFileCode(UUIDUtils.getFileCode(fileInfo.getFileSuffix()));
				fileRecord.setFileName(fileInfo.getFileName());
				fileRecord.setFileSize(fileInfo.getFileSize());
				fileRecord.setFileSuffix(fileInfo.getFileSuffix());
				fileRecord.setId(fileInfo.getFileKey());
				fileRecord.setLastModifyId(user.getId());
				fileRecord.setLastModifyName(user.getUserName());
				Date now = new Date();
				fileRecord.setCreateTime(now);
				fileRecord.setLastModifyTime(now);
				fileRecord.setMenuId(menu.getId());
				fileRecord.setValid("Y");
				fileRecordRepository.save(fileRecord);
				//保存文件操作记录
				FileOperateRecord fileOperateRecord = new FileOperateRecord();
				fileOperateRecord.setFileId(fileRecord.getId());
				fileOperateRecord.setFileName(fileRecord.getFileName());
				fileOperateRecord.setOperaterId(user.getId());
				fileOperateRecord.setOperaterName(user.getUserName());
				fileOperateRecord.setOperaterTime(now);
				fileOperateRecord.setOperaterType(Constants.OperaterType.CREATE.eval());
				fileOperateRecordRepository.save(fileOperateRecord);
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			//关闭文件流
			if(fileInfo.getFile()!=null){
				fileInfo.getFile().close();
			}
			if(pdfOs!=null){
				pdfOs.close();
			}
			if(pdfIs!=null){
				pdfIs.close();
			}
		}
	}
	
	/**
	 * 文件下载
	 * @param filekey
	 * @return
	 */
	public FileInfo downloadFile(String filekey) throws Exception{
		FileInfo fileInfo = null;
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(filekey, "Y");
		if(fileRecord!=null){
			fileInfo = new FileInfo();
			fileInfo.setFileKey(filekey);
			fileInfo.setFileSuffix(fileRecord.getFileSuffix());
			SaveFileResult result = fileUtils.getFile(fileInfo);
			if(result.isResult()){
				fileInfo.setDownFile(result.getDownFile());
				fileInfo.setFileName(fileRecord.getFileName());
			}else{
				throw new RuntimeException("文件已丢失");
			}
		}
		return fileInfo;
	}
	
}
