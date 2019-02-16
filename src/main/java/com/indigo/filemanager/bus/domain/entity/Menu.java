package com.indigo.filemanager.bus.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @Description:文件目录表
 * @author: qiaoyuxi
 * @time: 2019年2月15日 上午10:27:58
 */
@Data
@Entity
@Table(name = "bus_menu")
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50)
	private String menuName;//目录名称
	
	@Column
	private Long parentId;//父级目录ID
	
	@Column(length=20)
	private String businessSystemCode;//业务系统代码
}
