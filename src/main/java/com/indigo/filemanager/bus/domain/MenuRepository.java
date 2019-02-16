package com.indigo.filemanager.bus.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indigo.filemanager.bus.domain.entity.Menu;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月15日 上午11:12:37
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>{

	/**
	 * 查询二级目录
	 * @param menuName 目录名称，即文件后缀名
	 * @param businessSystemCode 业务系统代码
	 * @param rootId 根目录Id（999）
	 * @return
	 */
	public Menu findByMenuNameAndBusinessSystemCodeAndParentIdNot(String menuName,String businessSystemCode,Long rootId);
	
	/**
	 * 查询一级目录
	 * @param businessSystemCode 业务系统代码
	 * @param rootId 根目录Id（999）
	 * @return
	 */
	public Menu findByBusinessSystemCodeAndParentId(String businessSystemCode,Long rootId);
}
