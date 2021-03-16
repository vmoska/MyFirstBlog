package com.project.myblog.repository;

import java.util.Optional;
import java.util.Set;

import com.project.myblog.entity.Authority;
import com.project.myblog.entity.AuthorityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    
	Optional<Authority> findByName(AuthorityName name);
	
	Set<Authority> findByNameIn(Set<AuthorityName> authorityNames);

}
