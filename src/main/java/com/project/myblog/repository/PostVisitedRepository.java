package com.project.myblog.repository;


import java.util.Date;
import java.util.List;

import com.project.myblog.entity.PostVisited;
import com.project.myblog.payload.response.PostVisitedResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostVisitedRepository  extends JpaRepository<PostVisited,Long >{

	@Query("SELECT new com.project.myblog.payload.response.PostVisitedResponse("
			+ "p.id, "
			+ "SUM(CASE WHEN pv.userId = 0 AND pv.createdAt <= :endDate THEN 1 ELSE 0 END), "
			+ "SUM(CASE WHEN pv.userId <> 0 AND pv.createdAt <= :endDate THEN 1 ELSE 0 END) "
			+ ") "
			+ "FROM PostVisited pv "
			+ "RIGHT JOIN Post p ON p = pv.post "
			+ "WHERE pv.createdAt >= :startDate "
			+ "OR pv.createdAt IS NULL "
			+ "GROUP BY p.id "
			+ "ORDER BY COUNT(pv.post) DESC")
			List<PostVisitedResponse> getViews(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

		
}
