package com.project.myblog.repository;

import java.util.List;
import java.util.Optional;


import com.project.myblog.entity.DbFile;
import com.project.myblog.entity.User;
import com.project.myblog.payload.response.ReportAuthor;
import com.project.myblog.payload.response.ReportReader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);	
	
	Optional<User> findByUsernameOrEmail(String username, String email);
	
	Boolean existsByUsernameOrEmail(String username, String email);
	
	List<User> findAllByEnabledTrue();
	
	Optional<User> findByIdAndEnabledTrue(Long id);
	
	Optional<User> findByUsername(String username);
	Optional<User> findByUsernameAndEnabledTrue(String username);
    
    Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);	
	
	
	@Transactional
	@Modifying
	@Query(value="UPDATE user u SET "
			+ "u.banned_until = DATE_ADD(CURRENT_TIMESTAMP, INTERVAL :banDays DAY), "
			+ "is_enabled = 0 "
			+ "WHERE u.id=:userId", nativeQuery=true)
	void updateBannedUntil(@Param("userId") long userId, @Param("banDays") int banDays);

	@Query(value = "SELECT DISTINCT ua.user_id " +
			"FROM user_authorities ua JOIN authorities a ON ua.authority_id = a.id" +
			" WHERE a.name = :role", nativeQuery = true)
	List<Long> findUsersByRole(@Param("role") String role);

	@Query(value = "SELECT u.avatar FROM User u WHERE u.id = :userId")
	Optional<DbFile> findAvatarById(Long userId);

	
	@Query(value = "SELECT "
			+ "new com.project.myblog.payload.response.ReportAuthor("
				+ "u.id,"
				+ "u.username, "
				+ "(SELECT COUNT(p.id) FROM Post p WHERE p.author.id = u.id), "
				+ "(SELECT COUNT(pv.id) FROM PostVisited pv WHERE pv.post.author.id = u.id), "
				+ "(SELECT COALESCE(ROUND(AVG(r.rate),2),0) FROM Rating r where r.ratingId.post.author.id = u.id) "
			+ ")"
			+ "FROM User u " 
			+ "INNER JOIN u.authorities ua ON ua.name='ROLE_EDITOR' "
			+ "ORDER BY u.username")
	List<ReportAuthor> getReportAuthor();
	
	@Query(value = "SELECT "
			+ "new com.project.myblog.payload.response.ReportReader("
				+ "u.id,"
				+ "u.username, "
				+ "(SELECT COUNT(c) FROM Comment c WHERE c.commentAuthor.id = u.id), "
				+ "(SELECT COUNT(ad) FROM Advisory ad WHERE ad.advisoryId.comment.commentAuthor.id = u.id AND ad.advisoryStatus='CLOSED_WITH_CONSEQUENCE'), "
				+ "u.enabled"
			+ ")"
			+ "FROM User u " 
			+ "INNER JOIN u.authorities ua ON ua.name='ROLE_READER' "
			+ "ORDER BY u.username")
	List<ReportReader> getReportReader();
	
	@Query(value="SELECT p.author.username "
			+ "FROM Post p "
			+ "WHERE p.id = :postId ")
	String getAuthorUsernameByPostId(long postId);
	
	
	
}