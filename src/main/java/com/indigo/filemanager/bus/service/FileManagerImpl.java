package com.indigo.filemanager.bus.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
import com.indigo.filemanager.common.util.UUIDUtils;
import com.indigo.filemanager.common.persistence.FileUtils;
import com.indigo.filemanager.common.persistence.vo.FileInfo;
import com.indigo.filemanager.common.persistence.vo.SaveFileResult;
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
	 * @param userCode
	 * @throws Exception 
	 */
	public void uploadFile(FileInfo fileInfo,String userCode) throws Exception{
		//校验用户？？
		User user = userRepository.findByAccessKeyIdAndValid(userCode,"Y");
		if(user==null){
			throw new RuntimeException("无效用户");
		}
		ByteArrayOutputStream pdfOs = null;
		ByteArrayInputStream pdfIs = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();//复制inputstream用
		InputStream tempStream = null;//用于转换用
		try{
			//复制输入流
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fileInfo.getFile().read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			tempStream = new ByteArrayInputStream(baos.toByteArray());
			fileInfo.setFile(new ByteArrayInputStream(baos.toByteArray()));
			//转换pdf文件并保存
			if(true){//??哪些可以转换pdf
				pdfOs = (ByteArrayOutputStream)fileTransferService.transferPdf(tempStream,
						fileInfo.getFileSuffix(), "pdf");
				pdfIs = new ByteArrayInputStream(pdfOs.toByteArray());
				//保存pdf文件
				FileInfo pdfFileInfo = new FileInfo();
				pdfFileInfo.setFile(pdfIs);
				pdfFileInfo.setFileKey(fileInfo.getFileKey());
				pdfFileInfo.setFileSuffix("pdf");
				SaveFileResult resultPdf = fileUtils.saveFile(pdfFileInfo);
				if(!resultPdf.isResult()){
					throw new RuntimeException("文件持久化失败");
				}
			}
			//保存原文件
			SaveFileResult result = fileUtils.saveFile(fileInfo);
			if(!result.isResult()){
				throw new RuntimeException("文件持久化失败");
			}
			//保存关系数据库信息
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
		}catch(Exception ex){
			throw ex;
		}finally{
			//关闭流
			if(fileInfo.getFile()!=null){
				fileInfo.getFile().close();
			}
			if(pdfOs!=null){
				pdfOs.close();
			}
			if(pdfIs!=null){
				pdfIs.close();
			}
			if(baos!=null){
				baos.close();
			}
			if(tempStream!=null){
				tempStream.close();
			}
		}
	}
	
	/**
	 * 文件下载
	 * @param filekey
	 * @param userCode
	 * @return
	 */
	public FileInfo downloadFile(String filekey,String userCode) throws Exception{
		//校验用户？？
		User user = userRepository.findByAccessKeyIdAndValid(userCode,"Y");
		if(user==null){
			throw new RuntimeException("无效用户");
		}
		FileInfo fileInfo = null;
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(filekey, "Y");
		if(fileRecord==null){
			throw new RuntimeException("未查询到该文件信息");
		}
		//读取文件物理存储
		fileInfo = new FileInfo();
		fileInfo.setFileKey(filekey);
		fileInfo.setFileSuffix(fileRecord.getFileSuffix());
		SaveFileResult result = fileUtils.getFile(fileInfo);
		if(result.isResult()){
			fileInfo.setDownFile(result.getDownFile());
			fileInfo.setFileName(fileRecord.getFileName());
		}else{
			throw new RuntimeException("文件物理存储已丢失");
		}
		//保存文件下载记录
		FileOperateRecord fileOperateRecord = new FileOperateRecord();
		fileOperateRecord.setFileId(fileRecord.getId());
		fileOperateRecord.setFileName(fileRecord.getFileName());
		fileOperateRecord.setOperaterId(user.getId());
		fileOperateRecord.setOperaterName(user.getUserName());
		fileOperateRecord.setOperaterTime(new Date());
		fileOperateRecord.setOperaterType(Constants.OperaterType.DOWNLOAD.eval());
		fileOperateRecordRepository.save(fileOperateRecord);
		return fileInfo;
	}
	
	/**
	 * 文件删除(删除文件物理存储，关系数据库有效标志置为无效)
	 * @param filekey
	 * @param userCode
	 * @throws Exception
	 */
	public void deleteFile(String filekey,String userCode) throws Exception{
		//校验用户？？
		User user = userRepository.findByAccessKeyIdAndValid(userCode,"Y");
		if(user==null){
			throw new RuntimeException("无效用户");
		}
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(filekey, "Y");
		if(fileRecord==null){
			throw new RuntimeException("未查询到该文件信息");
		}
		//删除文件物理存储
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileKey(filekey);
		fileInfo.setFileSuffix(fileRecord.getFileSuffix());
		SaveFileResult result = fileUtils.delteFile(fileInfo);
		if(!result.isResult()){
			throw new RuntimeException("文件物理存储已丢失");
		}
		//删除文件PDF物理存储
		if(true){//??哪些可以转换pdf
			fileInfo.setFileSuffix("pdf");
			SaveFileResult resultPdf = fileUtils.delteFile(fileInfo);
			if(!resultPdf.isResult()){
				throw new RuntimeException("文件物理存储已丢失");
			}
		}
		//更新文件
		Date now = new Date();
		fileRecord.setLastModifyId(user.getId());
		fileRecord.setLastModifyName(user.getUserName());
		fileRecord.setLastModifyTime(now);
		fileRecord.setValid("N");
		fileRecordRepository.save(fileRecord);
		//保存文件删除记录
		FileOperateRecord fileOperateRecord = new FileOperateRecord();
		fileOperateRecord.setFileId(fileRecord.getId());
		fileOperateRecord.setFileName(fileRecord.getFileName());
		fileOperateRecord.setOperaterId(user.getId());
		fileOperateRecord.setOperaterName(user.getUserName());
		fileOperateRecord.setOperaterTime(now);
		fileOperateRecord.setOperaterType(Constants.OperaterType.DELETE.eval());
		fileOperateRecordRepository.save(fileOperateRecord);
	}
	
	/**
	 * 文件更新 (？？可更新的属性包含什么？？目前仅可更新文件名——不包含后缀，以及文件内容，文件大小)
	 * @param fileInfo
	 * @param userCode
	 * @throws Exception
	 */
	public void updateFile(FileInfo fileInfo,String userCode) throws Exception{
		//校验用户？？
		User user = userRepository.findByAccessKeyIdAndValid(userCode,"Y");
		if(user==null){
			throw new RuntimeException("无效用户");
		}
		//查询文件记录
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(fileInfo.getFileKey(), "Y");
		if(fileRecord==null){
			throw new RuntimeException("未查询到该文件信息");
		}
		ByteArrayOutputStream pdfOs = null;
		ByteArrayInputStream pdfIs = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();//复制inputstream用
		InputStream tempStream = null;//用于转换用
		try{
			//复制输入流
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fileInfo.getFile().read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			tempStream = new ByteArrayInputStream(baos.toByteArray());
			fileInfo.setFile(new ByteArrayInputStream(baos.toByteArray()));
			//转换pdf文件并保存
			if(true){//??哪些可以转换pdf
				pdfOs = (ByteArrayOutputStream)fileTransferService.transferPdf(tempStream,
						fileInfo.getFileSuffix(), "pdf");
				pdfIs = new ByteArrayInputStream(pdfOs.toByteArray());
				//更新pdf文件
				FileInfo pdfFileInfo = new FileInfo();
				pdfFileInfo.setFile(pdfIs);
				pdfFileInfo.setFileKey(fileInfo.getFileKey());
				pdfFileInfo.setFileSuffix("pdf");
				SaveFileResult resultPdf = fileUtils.updateFile(pdfFileInfo);
				if(!resultPdf.isResult()){
					throw new RuntimeException("文件持久化失败");
				}
			}
			//更新原文件
			SaveFileResult result = fileUtils.updateFile(fileInfo);
			if(!result.isResult()){
				throw new RuntimeException("文件持久化失败");
			}
			//保存关系数据库信息
			//更新文件
			Date now = new Date();
			fileRecord.setFileName(fileInfo.getFileName());
			fileRecord.setFileSize(fileInfo.getFileSize());
			fileRecord.setLastModifyId(user.getId());
			fileRecord.setLastModifyName(user.getUserName());
			fileRecord.setLastModifyTime(now);
			fileRecordRepository.save(fileRecord);
			//保存文件操作记录
			FileOperateRecord fileOperateRecord = new FileOperateRecord();
			fileOperateRecord.setFileId(fileRecord.getId());
			fileOperateRecord.setFileName(fileRecord.getFileName());
			fileOperateRecord.setOperaterId(user.getId());
			fileOperateRecord.setOperaterName(user.getUserName());
			fileOperateRecord.setOperaterTime(now);
			fileOperateRecord.setOperaterType(Constants.OperaterType.UPDATE.eval());
			fileOperateRecordRepository.save(fileOperateRecord);
		}catch(Exception ex){
			throw ex;
		}finally{
			//关闭流
			if(fileInfo.getFile()!=null){
				fileInfo.getFile().close();
			}
			if(pdfOs!=null){
				pdfOs.close();
			}
			if(pdfIs!=null){
				pdfIs.close();
			}
			if(baos!=null){
				baos.close();
			}
			if(tempStream!=null){
				tempStream.close();
			}
		}
	}
	
	/**
	 * 根据filekey获取pdf文件的输入流
	 * @param filekey
	 * @return
	 * @throws Exception
	 */
	public InputStream getFileFdf(String filekey) throws Exception{
		
		return null;
	}
}
