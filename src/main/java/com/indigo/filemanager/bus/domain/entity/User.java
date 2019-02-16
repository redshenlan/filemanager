package com.indigo.filemanager.bus.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @Description:用户表
 * @author: qiaoyuxi
 * @time: 2019年2月15日 上午11:42:55
 */
@Data
@Entity
@Table(name = "prv_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=20)
	private String userCode;//用户代码
	
	@Column(length=50)
	private String userName;//用户代码
	
	@Column(length=20)
	private String businessSystemCode;//业务系统代码
	
	@Column(length=1)
	private String valid;//有效标志（Y：有效，N，无效）
	
}
