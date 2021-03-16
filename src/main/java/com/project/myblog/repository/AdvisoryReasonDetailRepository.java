package com.project.myblog.repository;


import com.project.myblog.entity.AdvisoryReason;
import com.project.myblog.entity.AdvisoryReasonDetail;
import com.project.myblog.entity.AdvisoryReasonDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AdvisoryReasonDetailRepository extends JpaRepository<AdvisoryReasonDetail, AdvisoryReasonDetailId> {
	
	Optional<AdvisoryReasonDetail> findByEndDateEqualsAndAdvisoryReasonDetailIdAdvisoryReason(Date endDate, AdvisoryReason ar);

	
	
}
