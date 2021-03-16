package com.project.myblog.repository;

import com.project.myblog.entity.Advisory;
import com.project.myblog.entity.AdvisoryId;
import com.project.myblog.entity.AdvisoryStatus;
import com.project.myblog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface AdvisoryRepository extends JpaRepository<Advisory, AdvisoryId>{

	boolean existsByAdvisoryIdCommentAndAdvisoryStatus(Comment comment, AdvisoryStatus advisoryStatus);
	
	long countByAdvisoryStatusEquals(AdvisoryStatus status);
	
	@Query(value="SELECT COUNT(*) FROM advisory ad "
			+ "WHERE status= :status", nativeQuery=true)
	long countByAdvisoryStatusEqualsSQL(@Param("status") String status);

}
