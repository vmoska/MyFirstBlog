package com.project.myblog.repository;


import com.project.myblog.entity.AdvisoryReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvisoryReasonRepository extends JpaRepository<AdvisoryReason, Long>{
	
	Optional<AdvisoryReason> findByAdvisoryReasonName(String advisoryReasonName);

	@Query("SELECT r FROM AdvisoryReason r "
			+ "INNER JOIN AdvisoryReasonDetail d ON r.id = d.advisoryReasonDetailId.advisoryReason.id "
			+ "WHERE d.endDate = '9999-12-31'")
	List<AdvisoryReason> getAllValidAdvisoryReason();
	
	@Query("SELECT r FROM AdvisoryReason r "
			+ "INNER JOIN AdvisoryReasonDetail d ON r.id = d.advisoryReasonDetailId.advisoryReason.id "
			+ "WHERE d.endDate = :endDate "
			+ "AND r.id = :advisoryId")
	Optional<AdvisoryReason> getAdvisoryReason(@Param("advisoryId") long advisoryId, @Param("endDate") Date endDate);




}
