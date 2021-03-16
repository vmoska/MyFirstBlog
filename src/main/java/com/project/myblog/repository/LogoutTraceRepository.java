package com.project.myblog.repository;

import java.util.Optional;

import com.project.myblog.entity.LogoutTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LogoutTraceRepository extends JpaRepository<LogoutTrace,String> {

	Optional<LogoutTrace> findByTokenNotValid(String authToken);

	@Transactional
	@Modifying
    @Query(value="DELETE FROM logout_trace t WHERE t.expiration < CURRENT_TIMESTAMP", nativeQuery=true)
    void deleteExpiredTokens();
}
