package com.project.myblog.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.project.myblog.entity.MainLand;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.MainLandRepository;
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
public class MainLandController {
	
	@Autowired
	MainLandRepository mainLandRepository;

	@GetMapping("public/get-mainland-by-name-3/{mainLandName}")
	public ResponseEntity<ApiResponseCustom> getMainLand3(
			HttpServletRequest request,
			@PathVariable String mainLandName) {
		
		MainLand mainLand = mainLandRepository.getMainLandByNameJPQL(mainLandName);
		
		Object msg;
		HttpStatus status;
		ResponseEntityHandler response;
		
		if(mainLand != null) {
			msg = mainLand;
			status = HttpStatus.OK;
		
		} else {
			msg = "Mainland not presents";
			status = HttpStatus.NOT_FOUND;
		}
		
		response = new ResponseEntityHandler(msg, request, status);
		return response.getResponseEntity();	
	}
	
	@PostMapping("private/add-mainland")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> addMainland(
			HttpServletRequest request,
			@RequestParam @NotEmpty @NotBlank @Size(min=2, max=15) String mainLandName) {
		
		Object msg;
		HttpStatus status;
		ResponseEntityHandler response;
		
		MainLand mainLand = mainLandRepository.getMainLandByNameJPQL(mainLandName);
		
		if(mainLand == null){
			mainLandRepository.save(new MainLand(mainLandName));
			msg = "new mainland added";
			
		} else {
			msg = "Mainland already present";
		}
		
		status = HttpStatus.OK;
		response = new ResponseEntityHandler(msg, request, status);
		return response.getResponseEntity();		
	}

}