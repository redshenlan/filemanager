package com.indigo.filemanager.bus.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "bus_accesskey")
public class AccessKey {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition="varchar(32) COMMENT '用户标识'")
	private String accessKeyId;
	
	@Column(columnDefinition="varchar(32) COMMENT '密钥'")
	private String accessKeySecret;

}
