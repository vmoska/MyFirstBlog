package com.project.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CommentReactionTotalResponse {
	
	private long commentId;
	private long nrReaction;

}
