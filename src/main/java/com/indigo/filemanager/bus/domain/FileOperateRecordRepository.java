package com.indigo.filemanager.bus.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indigo.filemanager.bus.domain.entity.FileOperateRecord;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月15日 下午3:19:40
 */
@Repository
public interface FileOperateRecordRepository extends JpaRepository<FileOperateRecord, Long>{

}
