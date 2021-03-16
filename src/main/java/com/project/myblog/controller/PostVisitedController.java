package com.project.myblog.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.PostVisitedResponse;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.PostVisitedRepository;
import com.project.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PostVisitedController {
	
	@Autowired
	PostVisitedRepository postVisitedRepository;
	@Autowired
	UserService userService;
	
	@GetMapping("public/get-views")
	public ResponseEntity<ApiResponseCustom> getViews(
			HttpServletRequest request, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam Date startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam Date endDate){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		List<PostVisitedResponse> viewsList = postVisitedRepository.getViews(userService.adjustDate(startDate),userService.adjustDate(endDate));
		
		if (viewsList.isEmpty()) {
			response.setMsg("There aren't posts views");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		response.setMsg(viewsList);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
	}

}
