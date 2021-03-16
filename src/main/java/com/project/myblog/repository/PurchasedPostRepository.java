package com.project.myblog.repository;


import java.util.List;

import com.project.myblog.entity.PurchasedPost;
import com.project.myblog.entity.PurchasedPostId;
import com.project.myblog.entity.User;
import com.project.myblog.payload.response.PurchaseReportResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PurchasedPostRepository extends JpaRepository<PurchasedPost, PurchasedPostId> {

	@Query(value = "SELECT COALESCE(SUM(pc.cost),0) " +
            "FROM PurchasedPost pp INNER JOIN PostCost pc " +
            "ON pp.id.post.id = pc.id.post.id " +
            "AND pp.purchaseDate < pc.endDate " +
            "AND pp.purchaseDate > pc.id.startDate " +
            "WHERE pp.id.user = :u")
    Long getTotalSpentCredit(@Param("u") User u);
	
	
	@Query(value="SELECT new com.project.myblog.payload.response.PurchaseReportResponse("
			+ "u.username, "
			+ "(SELECT COALESCE(COUNT(*),0)"
			+ "FROM Comment c "
			+ "LEFT JOIN Advisory ad ON ad.advisoryId.comment=c "
			+ "WHERE c.commentAuthor = u AND "
			+ "(ad.advisoryStatus <> 'CLOSED_WITH_CONSEQUENCE' OR "
			+ "ad.advisoryStatus IS NULL)), "
			+ "(SELECT COALESCE(SUM(pc.cost),0) "
			+ "FROM PurchasedPost p "
			+ "LEFT JOIN PostCost pc ON pc.postCostId.post=p.purchasedPostId.post "
			+ "WHERE p.purchasedPostId.user = u AND "
			+ "cast(p.purchaseDate as date)>= pc.postCostId.startDate AND "
			+ "cast(p.purchaseDate as date)< pc.endDate), "
			+ ""
			+ "(SELECT COALESCE(COUNT(*),0)"
			+ "FROM Comment c "
			+ "LEFT JOIN Advisory ad ON ad.advisoryId.comment=c "
			+ "WHERE c.commentAuthor = u AND "
			+ "(ad.advisoryStatus <> 'CLOSED_WITH_CONSEQUENCE' OR "
			+ "ad.advisoryStatus IS NULL)) - "
			+ "(SELECT COALESCE(SUM(pc.cost),0) "
			+ "FROM PurchasedPost p "
			+ "LEFT JOIN PostCost pc ON pc.postCostId.post=p.purchasedPostId.post "
			+ "WHERE p.purchasedPostId.user = u AND "
			+ "cast(p.purchaseDate as date)>= pc.postCostId.startDate AND "
			+ "cast(p.purchaseDate as date)< pc.endDate)"
			+ ") "
			+ "FROM User u "
			+ "INNER JOIN u.authorities au ON au.name=com.project.myblog.entity.AuthorityName.ROLE_READER "
			+ "GROUP BY u")
	List<PurchaseReportResponse> purchaseReportResponse();
	
	
	@Query(value="SELECT "
			+ "v.username,"
			+ "v.totalGain,"
			+ "v.totalSpent,"
			+ "v.balance "
			+ "FROM credit_balance v", nativeQuery=true)
	List<Object> purchaseReportResponseObject();
	


}
