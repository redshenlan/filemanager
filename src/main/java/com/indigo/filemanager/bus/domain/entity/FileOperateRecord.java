package com.indigo.filemanager.bus.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @Description:文件操作记录表
 * @author: qiaoyuxi
 * @time: 2019年2月15日 下午3:13:54
 */
@Data
@Entity
@Table(name = "bus_fileOperateRecord")
public class FileOperateRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50)
	private String fileId;//文件ID
	
	@Column(length=255)
	private String fileName;//文件名称
	
	@Column(length=2)
	private String operaterType;//操作类型(CRUD)
	
	@Column
	private Date operaterTime;//操作时间
	
	@Column
	private Long operaterId;//操作人ID
	
	@Column(length=50)
	private String operaterName;//操作人名称
	
}
