package com.indigo.filemanager.bus.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

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
import com.indigo.filemanager.bus.exception.FileOperateFailureException;
import com.indigo.filemanager.bus.exception.FileOperateFailureExceptionEnum;
import com.indigo.filemanager.common.Constants;
import com.indigo.filemanager.common.OperaterTypeEnum;
import com.indigo.filemanager.common.persistence.FileUtils;
import com.indigo.filemanager.common.persistence.vo.FileInfo;
import com.indigo.filemanager.common.persistence.vo.SaveFileResult;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月11日 上午11:02:00
 */
@Service
@Slf4j
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
	 * 文件转换
	 * @param fileInfo
	 * @throws FileOperateFailureException 
	 */
	public void transferFile(FileInfo fileInfo) throws FileOperateFailureException{
		ByteArrayOutputStream pdfOs = null;
		ByteArrayInputStream pdfIs = null;
		try {
			//文件转换
			pdfOs = (ByteArrayOutputStream) fileTransferService
					.transferPdf(fileInfo.getFile(),
							fileInfo.getFileSuffix(), "pdf");
			pdfIs = new ByteArrayInputStream(pdfOs.toByteArray());
			//持久化PDF文件
			fileInfo.setFile(pdfIs);
			fileInfo.setFileSuffix("pdf");
			SaveFileResult result = fileUtils.saveFile(fileInfo);
			if (!result.isResult()) {
				throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_PERSISTENCE_FAIL);
			}
			//更新文件属性
			FileRecord fileRecord = fileRecordRepository.findByIdAndValid(fileInfo.getFileKey(), "Y");
			if(fileRecord==null){
				throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_RECORD_NOT_FOUND);
			}
			fileRecord.setPdfFlag("Y");
			fileRecordRepository.save(fileRecord);
		} catch (FileOperateFailureException ex) {
			throw ex;
		} catch (IOException ex) {
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.IO_ERROR,ex.getMessage());
		} finally {
			if (pdfOs != null) {
				try {
					pdfOs.close();
				} catch (IOException e) {
					log.error("文件流关闭失败,msg:"+e.getMessage());
				}
			}
			if (pdfIs != null) {
				try {
					pdfIs.close();
				} catch (IOException e) {
					log.error("文件流关闭失败,msg:"+e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 文件上传
	 * @param fileInfo
	 * @param user
	 * @throws FileOperateFailureException 
	 */
	public void uploadFile(FileInfo fileInfo,User user) throws FileOperateFailureException{
		//保存原文件
		SaveFileResult result = fileUtils.saveFile(fileInfo);
		if(!result.isResult()){
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_PERSISTENCE_FAIL);
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
		fileRecord.setPdfFlag("N");
		fileRecordRepository.save(fileRecord);
		//保存文件操作记录
		this.saveFileOperateRecord(fileRecord, user, OperaterTypeEnum.CREATE);
	}
	
	/**
	 * 文件下载
	 * @param filekey
	 * @param user
	 * @return FileOperateFailureException
	 */
	public FileInfo downloadFile(String filekey,User user) throws FileOperateFailureException{
		FileInfo fileInfo = null;
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(filekey, "Y");
		if(fileRecord==null){
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_RECORD_NOT_FOUND);
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
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_PERSISTENCE_NOT_FOUND);
		}
		//保存文件下载记录
		this.saveFileOperateRecord(fileRecord, user, OperaterTypeEnum.DOWNLOAD);
		return fileInfo;
	}
	
	/**
	 * 文件删除(删除文件物理存储，关系数据库有效标志置为无效)
	 * @param filekey
	 * @param user
	 * @throws FileOperateFailureException
	 */
	public void deleteFile(String filekey,User user) throws FileOperateFailureException{
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(filekey, "Y");
		if(fileRecord==null){
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_RECORD_NOT_FOUND);
		}
		//删除文件物理存储
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileKey(filekey);
		fileInfo.setFileSuffix(fileRecord.getFileSuffix());
		SaveFileResult result = fileUtils.delteFile(fileInfo);
		if(!result.isResult()){
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_PERSISTENCE_NOT_FOUND);
		}
		//删除文件PDF物理存储
		if("Y".equals(fileRecord.getPdfFlag())){
			fileInfo.setFileSuffix("pdf");
			SaveFileResult resultPdf = fileUtils.delteFile(fileInfo);
			if(!resultPdf.isResult()){
				throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_PERSISTENCE_NOT_FOUND);
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
		this.saveFileOperateRecord(fileRecord, user, OperaterTypeEnum.DELETE);
	}
	
	/**
	 * 更新文件(仅更新文件名称)
	 * @param filekey
	 * @param filename
	 * @param user
	 */
	public void updateFile(String filekey, String filename, User user)  throws FileOperateFailureException {
		// 查询文件记录
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(filekey, "Y");
		if (fileRecord == null) {
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_RECORD_NOT_FOUND);
		}
		// 更新文件
		Date now = new Date();
		fileRecord.setFileName(filename);
		fileRecord.setLastModifyId(user.getId());
		fileRecord.setLastModifyName(user.getUserName());
		fileRecord.setLastModifyTime(now);
		fileRecordRepository.save(fileRecord);
		// 保存文件操作记录
		this.saveFileOperateRecord(fileRecord, user, OperaterTypeEnum.UPDATE);
	}
	
	/**
	 * 根据filekey获取pdf文件的输入流
	 * @param filekey
	 * @param user
	 * @return
	 * @throws FileOperateFailureException
	 */
	public InputStream getFileFdf(String filekey,User user) throws FileOperateFailureException{
		FileInfo fileInfo = null;
		FileRecord fileRecord = fileRecordRepository.findByIdAndValid(filekey, "Y");
		if(fileRecord==null){
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_RECORD_NOT_FOUND);
		}
		if(!"Y".equals(fileRecord.getPdfFlag())){
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_CAN_NOT_VIEW);
		}
		//读取文件物理存储
		fileInfo = new FileInfo();
		fileInfo.setFileKey(filekey);
		fileInfo.setFileSuffix("pdf");
		SaveFileResult result = fileUtils.getFile(fileInfo);
		ByteArrayOutputStream outputStream = null;
		ByteArrayInputStream inputStream = null;
		if(result.isResult()){
			outputStream = (ByteArrayOutputStream) result.getDownFile();
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		}else{
			throw new FileOperateFailureException(FileOperateFailureExceptionEnum.FILE_PERSISTENCE_NOT_FOUND);
		}
		//保存文件查看记录
		this.saveFileOperateRecord(fileRecord, user, OperaterTypeEnum.READ);
		if(outputStream!=null){
			try {
				outputStream.close();
			} catch (IOException e) {
				log.error("文件流关闭失败,msg:"+e.getMessage());
			}
		}
		return inputStream;
	}
	
	/**
	 * 保存文件操作记录
	 * @param fileRecord
	 * @param user
	 * @param operaterType
	 */
	private void saveFileOperateRecord(FileRecord fileRecord,User user,OperaterTypeEnum operaterType){
		FileOperateRecord fileOperateRecord = new FileOperateRecord();
		fileOperateRecord.setFileId(fileRecord.getId());
		fileOperateRecord.setFileName(fileRecord.getFileName());
		fileOperateRecord.setOperaterId(user.getId());
		fileOperateRecord.setOperaterName(user.getUserName());
		fileOperateRecord.setOperaterTime(new Date());
		fileOperateRecord.setOperaterType(operaterType.getCode());
		fileOperateRecordRepository.save(fileOperateRecord);
	}
}
