package com.indigo.filemanager.bus.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indigo.filemanager.bus.domain.entity.BusinessSystem;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月15日 下午5:11:57
 */
@Repository
public interface BusinessSystemRepository extends JpaRepository<BusinessSystem, String>{

}
