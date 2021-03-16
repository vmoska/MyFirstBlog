package com.project.myblog.repository;

import java.util.List;

import com.project.myblog.entity.CommentReaction;
import com.project.myblog.entity.CommentReactionId;
import com.project.myblog.entity.CommentReactionName;
import com.project.myblog.payload.response.CommentReactionByUserResponse;
import com.project.myblog.payload.response.CommentReactionResponse;
import com.project.myblog.payload.response.CommentReactionTotalResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReactionId> {


	@Query(value = "SELECT new com.project.myblog.payload.response.CommentReactionResponse( "
			+ "p.id , "
			+ "c.id , "
			+ "COUNT(cr.reaction))"
			+ "FROM CommentReaction cr "
			+ "INNER JOIN Comment c ON c.id = cr.commentReactionId.comment "
			+ "INNER JOIN Post p ON p.id = c.post "
			+ "WHERE cr.reaction = :name "
			+ "GROUP BY c.id")
	List<CommentReactionResponse> findReactionsByName(@Param("name") CommentReactionName name);
	
	@Query(value = "SELECT new com.project.myblog.payload.response.CommentReactionTotalResponse("
			+ "cr.commentReactionId.comment.id,"
			+ "COUNT(cr.reaction) AS nReaction"
			+ ") "
			+ "FROM CommentReaction cr "
			+ "GROUP BY cr.commentReactionId.comment.id "
			+ "ORDER BY nReaction DESC"
			)
	List<CommentReactionTotalResponse> findTotalReactionsByComment();
	
	
	@Query("SELECT new com.project.myblog.payload.response.CommentReactionByUserResponse("
			+ "u.username,"
			+ "(SELECT COALESCE(COUNT(cr.reaction),0) FROM CommentReaction cr WHERE cr.commentReactionId.user = u ) AS nReaction"
			+ ") "
			+ "FROM CommentReaction cr "
			+ "RIGHT JOIN User u ON cr.commentReactionId.user = u "
			+ "JOIN u.authorities ua "
			+ "WHERE ua.name != com.project.myblog.entity.AuthorityName.ROLE_ADMIN "
			+ "GROUP BY u "
			+ "ORDER BY nReaction DESC")
			List<CommentReactionByUserResponse> countReactionByUser();
}
