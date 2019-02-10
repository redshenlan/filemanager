package com.indigo.filemanager.bus.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indigo.filemanager.bus.domain.entity.AccessKey;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {
	
	public AccessKey findByAccessKeyId(String accessKeyId);

}
