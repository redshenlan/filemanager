package com.indigo.filemanager.bus.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indigo.filemanager.bus.domain.entity.User;

/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月15日 上午11:46:26
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	/**
	 * 根据用户代码查询用户
	 * @param userCode 用户代码
	 * @param valid 有效标志
	 * @return
	 */
	public User findByUserCodeAndValid(String userCode,String valid);
}
