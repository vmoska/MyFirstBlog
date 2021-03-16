package com.project.myblog.payload.response;

import com.project.myblog.entity.CommentReactionName;
import com.sun.istack.NotNull;

import lombok.Getter;

@Getter
public class CommentReactionRequest {
	
	@NotNull
	private long commentId;
	
	@NotNull
	private CommentReactionName reaction;

}
