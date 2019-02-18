package com.indigo.filemanager.bus.service;

import com.indigo.filemanager.bus.domain.entity.User;

public interface AccessKeyManager {
	
	public User findByAccessKeyId(String accessKeyId);

}
