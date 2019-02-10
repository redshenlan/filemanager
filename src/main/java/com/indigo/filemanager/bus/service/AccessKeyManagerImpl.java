package com.indigo.filemanager.bus.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.indigo.filemanager.bus.domain.AccessKeyRepository;
import com.indigo.filemanager.bus.domain.entity.AccessKey;

@Service
public class AccessKeyManagerImpl implements AccessKeyManager {
	
	@Resource
	private AccessKeyRepository accessKeyRepository;

	@Override
	public AccessKey findByAccessKeyId(String accessKeyId) {
		return accessKeyRepository.findByAccessKeyId(accessKeyId);
	}

}
