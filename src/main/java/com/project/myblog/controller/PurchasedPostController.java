package com.project.myblog.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.project.myblog.entity.*;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.PurchaseReportResponse;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.PostCostRepository;
import com.project.myblog.repository.PostRepository;
import com.project.myblog.repository.PurchasedPostRepository;
import com.project.myblog.repository.UserRepository;
import com.project.myblog.service.AdvisoryService;
import com.project.myblog.service.CreditService;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PurchasedPostController {
	
	@Autowired
    UserService userService;
	@Autowired
    PurchasedPostRepository purchasedPostRepository;
	@Autowired
    PostRepository postRepository;
	@Autowired
    PostCostRepository postCostRepository;
	@Autowired
    CreditService creditService;
	@Autowired
    AdvisoryService advisoryService;
	@Autowired
    UserRepository userRepository;
	
	
	@PutMapping("private/purchase-post")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> purchasePost (
	        @RequestParam long postId,
            HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            response.setMsg("Post not found");
            response.setStatus(HttpStatus.NOT_FOUND);
            return response.getResponseEntity();
        }

        if(purchasedPostRepository.existsById(new PurchasedPostId(userService.getAuthenticatedUser(), post.get()))){
            response.setMsg("You have already purchased this post");
            response.setStatus(HttpStatus.FORBIDDEN);
            return response.getResponseEntity();
        }

        // calcolare i  commenti scritti dal reader - crediti
        // sottrarre dai crediti i commenti bannati
        Long userBalance = creditService.getUserBalance(userService.getAuthenticatedUser());

        // recuperare il costo dal post della data attuale
        Optional<PostCost> postCost = postCostRepository.findByEndDateEqualsAndPostCostIdPost(advisoryService.get9999Date(),post.get());
        if(postCost.isEmpty()){
            response.setMsg("Post not for sale");
            response.setStatus(HttpStatus.FORBIDDEN);
            return response.getResponseEntity();
        }

        if(userBalance < postCost.get().getCost()){
            response.setMsg("Insufficient funds");
            response.setStatus(HttpStatus.FORBIDDEN);
            return response.getResponseEntity();
        }

        PurchasedPost purchasedPost = new PurchasedPost(new PurchasedPostId(userService.getAuthenticatedUser(),post.get()));
        purchasedPostRepository.save(purchasedPost);

        response.setMsg("Post "+postId+" purchased");
        return response.getResponseEntity();
	}
	
	@GetMapping("private/report-purchase-post")	
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> addAdvisory(HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		

		Instant start = Instant.now();
		List<PurchaseReportResponse> list = purchasedPostRepository.purchaseReportResponse();
		Instant end = Instant.now();
		System.out.println("first way PurchaseReportResponse: "+Duration.between(start, end).toMillis());
		
		Instant start2 = Instant.now();
		List<Object> list2 = purchasedPostRepository.purchaseReportResponseObject();
		Instant end2 = Instant.now();
		System.out.println("first way Object: "+Duration.between(start2, end2).toMillis());
		
		response.setMsg(list2);
		return response.getResponseEntity();
	}
	
	@GetMapping("/private/report-purchase-post2")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseCustom> reportPurchasePost2(HttpServletRequest request) {

        ResponseEntityHandler response = new ResponseEntityHandler(request);

        Instant start = Instant.now();
        List<PurchaseReportResponse> listReport = new ArrayList<>();
        List<Long> users = userRepository.findUsersByRole(AuthorityName.ROLE_READER.toString());
        for(Long userId : users){
            User user  = userRepository.findById(userId).get();
            listReport.add(
                    new PurchaseReportResponse(
                            user.getUsername(),
                            creditService.getTotalGain(user),
                            creditService.getCreditsSpent(user),
                            creditService.getUserBalance(user)
                    )
            );
        }
        Instant end = Instant.now();
        System.out.println("Second way: "+Duration.between(start, end).toMillis());

        response.setMsg(listReport);
        return response.getResponseEntity();
    }
		
	
}
