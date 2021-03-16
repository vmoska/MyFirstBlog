package com.project.myblog.repository;

import java.util.List;

import com.project.myblog.entity.AdvisorySeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdvisorySeverityRepository extends JpaRepository<AdvisorySeverity, String>{
	
	
	boolean existsBySeverityValue(int value);
	
	List<AdvisorySeverity> findAllByOrderBySeverityValueAsc();
	

}
