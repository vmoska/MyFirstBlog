package com.project.myblog.service;

import java.util.Optional;

import com.project.myblog.entity.CommentReaction;
import com.project.myblog.entity.CommentReactionName;
import com.project.myblog.payload.response.ResponseEntityHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class CommentReactionService {
	
	public boolean compareReactionType(Optional<CommentReaction> crr, CommentReactionName reactionRequest, ResponseEntityHandler response) {
		
		if (crr.get().getReaction() == reactionRequest) {
				response.setMsg("Reaction already set");
				response.setStatus(HttpStatus.BAD_REQUEST);
				return true;
	}
		return false;
		
	}

}
