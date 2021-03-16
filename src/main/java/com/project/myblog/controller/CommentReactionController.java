package com.project.myblog.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.project.myblog.entity.*;
import com.project.myblog.payload.response.*;
import com.project.myblog.repository.AdvisoryRepository;
import com.project.myblog.repository.CommentReactionRepository;
import com.project.myblog.repository.CommentRepository;
import com.project.myblog.service.CommentReactionService;
import com.project.myblog.service.CommentService;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CommentReactionController {

	@Autowired
	CommentReactionRepository commentReactionRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	AdvisoryRepository advisoryRepository;
	@Autowired
	UserService userService;
	@Autowired
	CommentService commentService;
	@Autowired
	CommentReactionService commentReactionService;
	
	@PostMapping("private/add-reaction")
	@PreAuthorize("hasRole('READER') or hasRole('EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> addReaction(
			@RequestBody @Valid CommentReactionRequest commentReactionRequest,
			HttpServletRequest request) {

		ResponseEntityHandler response = new ResponseEntityHandler(request);

		User u = userService.getAuthenticatedUser();

		Optional<Comment> c = commentRepository.findById(commentReactionRequest.getCommentId());

		if (commentService.commentNotFound(c, response))
			return response.getResponseEntity();

		/*
		 * if (!c.isPresent()) { response.setMsg("Comment does not exist");
		 * response.setStatus(HttpStatus.BAD_REQUEST); return
		 * response.getResponseEntity(); }
		 */

		/*
		 * if (advisoryRepository.existsByAdvisoryIdCommentAndAdvisoryStatus(c.get(),
		 * AdvisoryStatus.CLOSED_WITH_CONSEQUENCE)) { response.setMsg("Banned comment");
		 * response.setStatus(HttpStatus.BAD_REQUEST); return
		 * response.getResponseEntity();
		 * 
		 * }
		 */

		if (commentService.isCommentBanned(c, response))
			return response.getResponseEntity();

		if (userService.compareTwoUser(c.get().getCommentAuthor(), u, response))
			return response.getResponseEntity();

		/*
		 * if (c.get().getCommentAuthor() == u) {
		 * response.setMsg("Can't add reaction to your own comment");
		 * response.setStatus(HttpStatus.BAD_REQUEST); return
		 * response.getResponseEntity(); }
		 */

		CommentReactionId cri = new CommentReactionId(c.get(), u);

		Optional<CommentReaction> crr = commentReactionRepository.findById(cri);

		if (crr.isPresent()) {

			if (commentReactionService.compareReactionType(crr, commentReactionRequest.getReaction(), response))
				return response.getResponseEntity();

			/*
			 * if (crr.get().getReaction() == commentReactionRequest.getReaction()) {
			 * response.setMsg("Reaction already set");
			 * response.setStatus(HttpStatus.BAD_REQUEST); return
			 * response.getResponseEntity();
			 * 
			 * }
			 */
			
			else {
				crr.get().setReaction(commentReactionRequest.getReaction());
				response.setMsg("Reaction updated");
				response.setStatus(HttpStatus.OK);
				return response.getResponseEntity();
			}
		}
		CommentReaction cr = new CommentReaction(cri, commentReactionRequest.getReaction());
		commentReactionRepository.save(cr);

		response.setMsg("Reaction added");
		response.setStatus(HttpStatus.CREATED);
		return response.getResponseEntity();

	}
	
	@GetMapping("private/find-reaction-by-name")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> findReactionByName(
			@RequestParam String name,
			HttpServletRequest request) {
		
		CommentReactionName commentReactionName = CommentReactionName.valueOf(name.toUpperCase());
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		List<CommentReactionResponse> reactionList = commentReactionRepository.findReactionsByName(commentReactionName);
		
		if (reactionList.isEmpty()) {
			response.setMsg("There is not this kind of reaction");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		response.setMsg(reactionList);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
		
	}
	
	@GetMapping("private/find-reaction-by-comment")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> findTotReactionByComment( HttpServletRequest request) {
		
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		List<CommentReactionTotalResponse> reactionList = commentReactionRepository.findTotalReactionsByComment();
		
		if (reactionList.isEmpty()) {
			response.setMsg("There is not this kind of reaction");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		response.setMsg(reactionList);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
		
	}
	
	@GetMapping("private/count-reaction-by-user")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> countReactionByUser( HttpServletRequest request) {
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		List<CommentReactionByUserResponse> reactionList = commentReactionRepository.countReactionByUser();
		
		if (reactionList.isEmpty()) {
			response.setMsg("There is not this kind of reaction");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		response.setMsg(reactionList);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
		
	}
}
