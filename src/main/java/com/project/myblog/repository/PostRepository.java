package com.project.myblog.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.project.myblog.entity.Post;
import com.project.myblog.payload.response.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
	
	boolean existsByTitle(String title);
	boolean existsByIdAndPublishedTrue(long id);
	
	Optional<Post> findByTitle(String title);

	
	@Query(value="SELECT new com.project.myblog.payload.response.PostDetailResponse("
			+ "p.id,"
			+ "p.title,"
			+ "p.overview,"			
			+ "p.updatedAt,"
			+ "p.author.username,"
			+ "p.language.langName,"
			+ "(SELECT COALESCE(ROUND(AVG(r.rate),2),0) FROM Rating r WHERE r.ratingId.post.id = p.id AND r.ratingId.rater.enabled = true ) AS avg ) "
			+ "FROM Post p "
			+ "WHERE p.published = true "
			+ "AND p.id = :id")
	PostDetailResponse getPostDetail(@Param("id") long id);
	
	
	@Query(value="SELECT p from Post p "
			+ "LEFT JOIN FETCH p.countries "
			+ "WHERE p.id = :id")
	Optional<Post> getPostWithCountries(@Param("id") long id);
	
	@Query(value="SELECT p from Post p "
			+ "LEFT JOIN FETCH p.countries c "
			+ "LEFT JOIN FETCH p.author "
			+ "LEFT JOIN FETCH p.language "
			+ "WHERE :countryCode IN c.countryCode "
			+ "AND p.published=true")
	List<Post>getPostCountriesResponse(@Param("countryCode") String countryCode);
	
	@Query(value="SELECT p from Post p "
			+ "LEFT JOIN FETCH p.countries c "
			+ "LEFT JOIN FETCH p.author "
			+ "LEFT JOIN FETCH p.language "
			+ "WHERE :mainLandName IN c.mainLand.mainLandName "
			+ "AND p.published=true "
			+ "GROUP BY p.id "
			)
	List<Post>getPostMainLandResponse(@Param("mainLandName")String mainLandName);
	
	@Modifying
	@Transactional
	@Query(value="DELETE FROM post_tags WHERE tag_id = :tagName", nativeQuery=true)
	void deleteTagFromPost(@Param("tagName") String TagName);
	

	@Query(value="SELECT new com.project.myblog.payload.response.PostTagResponse("
			+ "p.id,"
			+ "p.title,"
			+ "p.overview,"
			+ "p.updatedAt,"
			+ "p.author.username,"
			+ "p.language.langName,"
			+ "CAST(:tagName AS string)) "
			+ "FROM Post p "
			+ "JOIN p.tags t "
			+ "WHERE t.tagName in(:tagName)")
	List<PostTagResponse> getPostsByTag(@Param("tagName") String tagName);
	
	@Query(value="SELECT new com.project.myblog.payload.response.PostResponse("
			+ "p.id,"
			+ "p.title,"
			+ "p.overview,"
			+ "p.updatedAt,"
			+ "p.author.username,"
			+ "p.language.langName,"
			+ "COALESCE(ROUND(AVG(r.rate),2),0) )"
			+ "FROM Post p "
			+ "LEFT JOIN Rating r ON r.ratingId.post.id = p.id "
			+ "WHERE :authorName = p.author.username "
			+ "AND p.published = true "
			+ "AND r.ratingId.rater.enabled = true "
			+ "GROUP BY p.id")
	List<PostResponse> getPostsByAuthor(@Param("authorName") String authorName);
	
	@Query(value="SELECT new com.project.myblog.payload.response.PostCountCommentsResponse("
			+ "p.id,"
			+ "p.title,"
			+ "p.overview,"
			+ "p.updatedAt,"
			+ "p.author.username,"
			+ "COUNT(c.id))"
			+ "FROM Post p "
			+ "LEFT JOIN Comment c ON p.id=c.post "
			+ "WHERE p.published=true GROUP BY p.id")
	List<PostCountCommentsResponse> getPostCountCommentResponse();
	
	@Query(value="SELECT new com.project.myblog.payload.response.PostCountTagResponse("
			+ "p.id,"
			+ "p.title,"
			+ "p.overview,"
			+ "p.updatedAt,"
			+ "p.author.username,"
			+ "size(ts) )"
			+ "FROM Post p "
			+ "LEFT JOIN p.tags ts "
			+ "WHERE p.published=true GROUP BY p.id")
	List<PostCountTagResponse> getPostCountTagsResponse();
	
	@Query(value="SELECT new com.project.myblog.payload.response.PostResponse("
			+ "p.id,"
			+ "p.title,"
			+ "p.overview,"
			+ "p.updatedAt,"
			+ "p.author.username,"
			+ "p.language.langName,"
			+ "COALESCE(ROUND(AVG(r.rate),2),0) )"
			+ "FROM Post p "
			+ "LEFT JOIN Rating r ON r.ratingId.post.id = p.id "
			+ "WHERE r.ratingId.rater.enabled = true "
			+ "GROUP BY p.id "
			+ "ORDER BY "
			+ "CASE WHEN :ordered = 'ASC' THEN COALESCE(ROUND(AVG(r.rate),2),0) END ASC, "
			+ "CASE WHEN :ordered = 'DESC' THEN COALESCE(ROUND(AVG(r.rate),2),0) END DESC"
			)
	List<PostResponse> getPostsByAvg(@Param("ordered") String ordered);
	
	Optional<Post> findByIdAndPublishedTrue(long id);
	
	
	@Query(value = "SELECT new com.project.myblog.payload.response.ReportPost("
			+ "p.id ,"
			+ "p.title , "
			+ "p.author.username , "
			+ "ROUND(AVG(CASE WHEN r.rate IS NULL THEN 0 ELSE r.rate END),2), "
			+ "p.published , "
			+ "p.approved, "
			+ "(SELECT COALESCE(COUNT(pv2.id),0) FROM PostVisited pv2 WHERE pv2.post.id = pv.post.id))"
			+ "FROM Post  p "
			+ "LEFT JOIN Rating r ON r.ratingId.post.id = p.id "
			+ "LEFT JOIN PostVisited pv ON pv.post.id = p.id "
			+ "GROUP BY p.id  "
			+ "ORDER BY p.id ASC")
	List<ReportPost> getReportPost();
	
	long countByApprovedTrue();
	long countByPublishedTrue();
	
	@Query(value="SELECT new com.project.myblog.payload.response.PostSearch("
			+ "p.id, "
			+ "p.title, "
			+ "p.content, "
			+ "p.author.username) "
			+ "FROM Post p "
			+ "WHERE p.published = true"
			)
	List<PostSearch> getPostsVisibleForSearch();
	
	@Query(value="SELECT p.* FROM post p "
			+ " WHERE p.is_published=true "
			+ " AND   REGEXP_LIKE(p.title, BINARY :wordToFind ) OR REGEXP_LIKE(p.content, BINARY :wordToFind )",nativeQuery = true)
	List<Post> getPostsVisibleBySearchCaseSensitiveTrue(String wordToFind);
	
	@Query(value="SELECT p.* FROM post p "
			+ " WHERE p.is_published=true "
			+ " AND   REGEXP_LIKE(p.title, :wordToFind ) OR REGEXP_LIKE(p.content,:wordToFind )",nativeQuery = true)
	List<Post> getPostsVisibleBySearchCaseSensitiveFalse(String wordToFind);
	
	@Query(value="SELECT p.id,p.title,p.content,u.username " +
			"FROM post p JOIN user u ON p.author = u.id " +
			"WHERE REGEXP_LIKE (p.title, :pattern,:caseSensitivity) " +
			"OR REGEXP_LIKE (p.content, :pattern,:caseSensitivity)",nativeQuery = true)
	List<Object> getPostByKeyword(@Param("pattern") String pattern,@Param("caseSensitivity") String caseSensitivity);

	
	
	
	
}
