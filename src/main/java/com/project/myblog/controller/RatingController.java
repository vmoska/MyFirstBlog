package com.project.myblog.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.project.myblog.entity.Post;
import com.project.myblog.entity.Rating;
import com.project.myblog.entity.RatingId;
import com.project.myblog.entity.User;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.AuthorAverageResponse;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.PostRepository;
import com.project.myblog.repository.RatingRepository;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class RatingController {
	
	@Autowired
	RatingRepository ratingRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	UserService userService;
	
	@PostMapping("private/add-rate/{postId}/{vote}")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> addComment(
			@PathVariable @NotNull long postId,
			@PathVariable @NotNull @Min(1) @Max(5) @Digits(integer = 1, fraction = 0) int vote,
			HttpServletRequest request)   {
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		Optional<Post> p = postRepository.findById(postId);
		if (p.isEmpty()) {
			response.setMsg("Post is not present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
				
		User u = userService.getAuthenticatedUser();
		
		try {
			ratingRepository.save(new Rating(new RatingId(u,p.get()),vote));
		
		}catch(Exception ex) {
			response.setMsg(ex.getCause().getCause().getLocalizedMessage());
			response.setStatus(HttpStatus.BAD_REQUEST);
			return response.getResponseEntity();
		}
		
		response.setMsg("New vote added to post: "+postId);
		response.setStatus(HttpStatus.CREATED);
		return response.getResponseEntity();
	}
	
	@GetMapping("public/get-author-average")
	public ResponseEntity<ApiResponseCustom> getAuthorAverage(
			@RequestParam String authorName,
			HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		AuthorAverageResponse authorAverageResponse = ratingRepository.getAuthorAverage(authorName);
		if(authorAverageResponse == null) {
			response.setMsg("Author not found");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		response.setMsg(authorAverageResponse);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
		
	}

}
