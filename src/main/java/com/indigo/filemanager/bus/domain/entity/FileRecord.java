package com.indigo.filemanager.bus.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @Description:文件记录表
 * @author: qiaoyuxi
 * @time: 2019年2月15日 下午2:41:20
 */
@Data
@Entity
@Table(name = "bus_file")
public class FileRecord {

	@Id
	@Column(length=32)
	private String id;
	
	@Column
	private Long menuId;//目录ID
	
	@Column(length=50)
	private String fileCode;//文件编码
	
	@Column(length=255)
	private String fileName;//文件名称
	
	@Column(length=10)
	private String fileSuffix;//文件后缀
	
	@Column
	private Long fileSize;//文件大小
	
	@Column
	private Date createTime;//创建时间
	
	@Column
	private Date lastModifyTime;//最后修改时间
	
	@Column
	private Long lastModifyId;//最后修改人ID
	
	@Column(length=50)
	private String lastModifyName;//最后修改人名称
	
	@Column(length=1)
	private String valid;//有效标志（Y：正常，N，已删除）
}
