package com.project.myblog.controller;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.project.myblog.entity.Post;
import com.project.myblog.entity.PostCost;
import com.project.myblog.entity.PostCostId;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.PostCostRepository;
import com.project.myblog.repository.PostRepository;
import com.project.myblog.service.AdvisoryService;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class PostCostController {
	
	@Autowired
	PostRepository postRepository;
	@Autowired
	PostCostRepository postCostRepository;
	@Autowired
	AdvisoryService advisoryService;
	@Autowired
	UserService userService;
	
	@PutMapping("private/add-cost-to-post")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> addCost(
			@RequestParam @Min(1) @Max(99) int cost,
			@RequestParam long postId, HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		  Optional<Post> post = postRepository.findById(postId);

	        if(post.isEmpty()){
	            response.setMsg("Post not found");
	            response.setStatus(HttpStatus.NOT_FOUND);
	            return response.getResponseEntity();
	        }
	        
	        Date adjustDate = userService.adjustDate(new Date());

	        Optional<PostCost> postCost = postCostRepository.findByEndDateEqualsAndPostCostIdPost(
	        		advisoryService.get9999Date(),post.get());

	        if(postCost.isPresent()){
	            if(postCost.get().getCost() == cost){
	                response.setMsg("The post cost must be different");
	                response.setStatus(HttpStatus.FORBIDDEN);
	                return response.getResponseEntity();
	            }
	            postCost.get().setEndDate(adjustDate);
	            postCostRepository.save(postCost.get());
	        }

	        PostCostId postCostId = new PostCostId(post.get(),adjustDate);
	        PostCost newPostCost = new PostCost(postCostId,advisoryService.get9999Date(),cost);
	        postCostRepository.save(newPostCost);

	        response.setMsg("Cost added to post");
	        return response.getResponseEntity();
	}

}
