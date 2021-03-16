package com.project.myblog.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.project.myblog.entity.Comment;
import com.project.myblog.entity.Post;
import com.project.myblog.entity.User;
import com.project.myblog.payload.request.CommentRequest;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.CommentRepository;
import com.project.myblog.repository.PostRepository;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class CommentController {
	
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	UserService userService;
	
	@PostMapping("private/add-comment/{id}")
	@PreAuthorize("hasRole('READER') or hasRole('EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> addComment(
			@PathVariable @NotNull long id,
			@RequestBody @Valid CommentRequest commentRequest,
			HttpServletRequest request) {
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);

		Optional<Post> p = postRepository.findById(id);

		if (p.isEmpty()) {
			response.setMsg("Post is not present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		User u = userService.getAuthenticatedUser();
		
		Comment c = new Comment(commentRequest.getComment(), u, p.get());
		commentRepository.save(c);
		
		if(commentRequest.getRefererTo() > 0) {
			Optional<Comment> refererTo = commentRepository.findById(commentRequest.getRefererTo());

			if (refererTo.isEmpty() || id != refererTo.get().getPost().getId()) {
				response.setMsg("Comment (father) is not present");
				response.setStatus(HttpStatus.NOT_FOUND);
				return response.getResponseEntity();
			}	
			
			c.getReferersTo().add(refererTo.get());
		}
		
		response.setMsg("New comment added to post: "+id);
		response.setStatus(HttpStatus.CREATED);
		return response.getResponseEntity();
		
	}

}
