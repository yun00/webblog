package com.yun.hello.repository;

import com.yun.hello.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Authority 仓库.
 *
 *
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
