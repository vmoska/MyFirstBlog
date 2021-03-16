package com.project.myblog.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor
public class CommentReactionId implements Serializable {

	@ManyToOne
	@JoinColumn(name = "COMMENT_ID", nullable = false)
	private Comment comment;
	
	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
}
