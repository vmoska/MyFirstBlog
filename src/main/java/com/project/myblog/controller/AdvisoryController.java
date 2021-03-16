package com.project.myblog.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.project.myblog.entity.*;
import com.project.myblog.payload.request.AdvisoryIdRequest;
import com.project.myblog.payload.request.AdvisoryRequest;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.*;
import com.project.myblog.service.AdvisoryService;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AdvisoryController {
	
	@Autowired
	AdvisoryReasonRepository advisoryReasonRepository;
	@Autowired
	AdvisoryRepository advisoryRepository;
	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	AdvisoryService advisoryService;
	@Autowired
	AdvisoryReasonDetailRepository advisoryReasonDetailRepository;
	
	@PostMapping("private/add-advisory")	
	public ResponseEntity<ApiResponseCustom> addAdvisory(
			@Valid @RequestBody AdvisoryRequest advisoryRequest,
			HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		Optional<Comment> c = commentRepository.getCommentAndAuthor(advisoryRequest.getCommentId());
		if(c.isEmpty()) {
			response.setMsg("Comment does not exist");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		User u = userService.getAuthenticatedUser();
		
		if(u.getId().equals(c.get().getCommentAuthor().getId())) {
			response.setMsg("You cannot report yourself !");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}

		Optional<AdvisoryReason> ar = advisoryReasonRepository.getAdvisoryReason(
				advisoryRequest.getAdvisoryReasonId(), advisoryService.get9999Date());

		if(ar.isEmpty()){
			response.setMsg("Reason does not exist");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}

		AdvisoryId aId = new AdvisoryId(c.get(), u, ar.get());
		if(advisoryRepository.existsById(aId)) {
			response.setMsg("You alredy reported this comment for the same reason");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}

		Advisory a = new Advisory(aId, advisoryRequest.getDescription());
		advisoryRepository.save(a);

		response.setMsg("New Advisory added");

		return response.getResponseEntity();
	}
	
	@PutMapping("private/change-status-advisory")	
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> addAdvisory(
			@Valid @RequestBody AdvisoryIdRequest advisoryIdRequest,
			HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		Optional<Comment> c = commentRepository.findById(advisoryIdRequest.getCommentId());

		if(c.isEmpty()) {
			response.setMsg("No comment found");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		Optional<User> u = userRepository.findById(advisoryIdRequest.getUserId());

		if(u.isEmpty()) {
			response.setMsg("No user found");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		Optional<AdvisoryReason> ar = advisoryReasonRepository.findById(
				advisoryIdRequest.getAdvisoryReasonId());

		if(ar.isEmpty()) {
			response.setMsg("No advisory reason found");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		

		Optional<Advisory> a = advisoryRepository.findById(
				new AdvisoryId(c.get(), u.get(), ar.get()));
		
		if(a.get().getAdvisoryStatus().equals(advisoryIdRequest.getStatus())) {
			response.setMsg("No new status to update");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}

		
		if(a.get().getAdvisoryStatus() == AdvisoryStatus.CLOSED_WITH_CONSEQUENCE || 
				a.get().getAdvisoryStatus() == AdvisoryStatus.CLOSED_WITHOUT_CONSEQUENCE){
			response.setMsg("Advisory already closed");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}
		
		if(a.get().getAdvisoryStatus().compareTo(advisoryIdRequest.getStatus()) > 0){
			response.setMsg("Invalid advisory status for this instance");
			response.setStatus(HttpStatus.FORBIDDEN);
			return response.getResponseEntity();
		}
		
		//ban comment author
		
		if(advisoryIdRequest.getStatus() == AdvisoryStatus.CLOSED_WITH_CONSEQUENCE) {
			// update banned_until in table user: now + severity_value
			Optional<AdvisoryReasonDetail> ard = advisoryReasonDetailRepository.findByEndDateEqualsAndAdvisoryReasonDetailIdAdvisoryReason(advisoryService.get9999Date(), ar.get() );
			int banDays = ard.get().getAdvisorySeverity().getSeverityValue();
			userRepository.updateBannedUntil(c.get().getCommentAuthor().getId(), banDays);
		}
		
		
		a.get().setAdvisoryStatus(advisoryIdRequest.getStatus());
		advisoryRepository.save(a.get());
		response.setMsg("Advisory status has been updated");
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
		
	}
	

}
