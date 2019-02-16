package com.indigo.filemanager.bus.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indigo.filemanager.bus.domain.entity.FileRecord;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月15日 下午3:19:24
 */
@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, String>{

	public FileRecord findByIdAndValid(String id,String valid);
}
