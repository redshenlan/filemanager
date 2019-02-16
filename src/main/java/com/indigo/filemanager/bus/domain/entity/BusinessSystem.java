package com.indigo.filemanager.bus.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月15日 下午5:06:47
 */
@Data
@Entity
@Table(name = "bus_businessSystem")
public class BusinessSystem {

	@Id
	@Column(length=20)
	private String businessSystemCode;
	
	@Column(length=50)
	private String businessSystemName;//业务系统名称
	
	@Column(length=1)
	private String valid;//有效标志（Y：有效，N，无效）
}
