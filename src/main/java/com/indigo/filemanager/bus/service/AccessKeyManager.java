package com.indigo.filemanager.bus.service;

import com.indigo.filemanager.bus.domain.entity.AccessKey;

public interface AccessKeyManager {
	
	public AccessKey findByAccessKeyId(String accessKeyId);

}
