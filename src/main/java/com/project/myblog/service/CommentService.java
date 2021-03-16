package com.project.myblog.service;

import com.project.myblog.entity.AdvisoryStatus;
import com.project.myblog.entity.Comment;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.AdvisoryRepository;
import com.project.myblog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CommentService {
	
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	AdvisoryRepository advisoryRepository;
	
	public boolean commentNotFound(Optional<Comment> c , ResponseEntityHandler response) {
		
		if (c.isEmpty()) {
			response.setMsg("Comment does not exist");
			response.setStatus(HttpStatus.BAD_REQUEST);
			return true;
		}
		return false;
	}
	
	public boolean isCommentBanned(Optional<Comment> c , ResponseEntityHandler response) {
		
		if (advisoryRepository.existsByAdvisoryIdCommentAndAdvisoryStatus(c.get(),
				AdvisoryStatus.CLOSED_WITH_CONSEQUENCE)) {
			response.setMsg("Banned comment");
			response.setStatus(HttpStatus.BAD_REQUEST);
			return true;

		}
		return false;
	}

}
