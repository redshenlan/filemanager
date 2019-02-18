package com.indigo.filemanager.bus.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.indigo.filemanager.bus.domain.UserRepository;
import com.indigo.filemanager.bus.domain.entity.User;

@Service
public class AccessKeyManagerImpl implements AccessKeyManager {
	
	@Resource
	private UserRepository userRepository;

	@Override
	public User findByAccessKeyId(String accessKeyId) {
		return userRepository.findByAccessKeyIdAndValid(accessKeyId, "Y");
	}

}
