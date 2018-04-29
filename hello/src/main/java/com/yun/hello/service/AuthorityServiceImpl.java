/**
 * 
 */
package com.yun.hello.service;

import com.yun.hello.domain.Authority;
import com.yun.hello.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Authority 服务.
 * 
 * @since 1.0.0 2017年3月30日
 */
@Service
public class AuthorityServiceImpl  implements AuthorityService {
	
	@Autowired
	private AuthorityRepository authorityRepository;

	/**
	 * 根据ID查找用色ß
	 * @param id
	 * @return
	 */
	@Override
	public Authority getAuthorityById(Long id) {
		return authorityRepository.findOne(id);
	}

}
