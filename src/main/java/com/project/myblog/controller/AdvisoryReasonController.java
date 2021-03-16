package com.project.myblog.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;


import com.project.myblog.entity.AdvisoryReason;
import com.project.myblog.entity.AdvisoryReasonDetail;
import com.project.myblog.entity.AdvisoryReasonDetailId;
import com.project.myblog.entity.AdvisorySeverity;
import com.project.myblog.payload.request.AdvisoryReasonRequest;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.AdvisoryReasonDetailRepository;
import com.project.myblog.repository.AdvisoryReasonRepository;
import com.project.myblog.repository.AdvisorySeverityRepository;
import com.project.myblog.service.AdvisoryService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class AdvisoryReasonController {
	
	@Autowired
	AdvisoryReasonRepository advisoryReasonRepository;
	@Autowired
	AdvisoryReasonDetailRepository advisoryReasonDetailRepository;
	@Autowired
	AdvisorySeverityRepository advisorySeverityRepository;
	@Autowired
	AdvisoryService advisoryService;
	
	
	@PostMapping("private/add-advisory-reason")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> addAdvisoryReason(
			@Valid @RequestBody AdvisoryReasonRequest advisoryReasonRequest,
			HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);
		
		
		Optional<AdvisoryReason> ar = advisoryReasonRepository.findByAdvisoryReasonName(advisoryReasonRequest.getAdvisoryReasonName());
		
		Optional<AdvisorySeverity> as = advisorySeverityRepository.findById(advisoryReasonRequest.getAdvisorySeverity().toUpperCase());

		if(as.isEmpty()) {
			response.setMsg("AdvisorySeverity is not present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		AdvisoryReason arNew = new AdvisoryReason();
		
		if(ar.isPresent()) {			
			Optional<AdvisoryReasonDetail> ard = advisoryReasonDetailRepository.findByEndDateEqualsAndAdvisoryReasonDetailIdAdvisoryReason(advisoryService.get9999Date(), ar.get() );
			
			if(ard.get().getAdvisorySeverity().getSeverityDescription().equalsIgnoreCase(advisoryReasonRequest.getAdvisorySeverity())) {
				response.setMsg("You have to change the severity");
				response.setStatus(HttpStatus.FORBIDDEN);
				return response.getResponseEntity();
			}
			
			ard.get().setEndDate(DateUtils.addDays(advisoryReasonRequest.getStartDate(),-1));
			AdvisoryReasonDetail ardNew = new AdvisoryReasonDetail(
					new AdvisoryReasonDetailId(ar.get(), advisoryReasonRequest.getStartDate()),
					advisoryService.get9999Date(),
					as.get()
					);

			response.setMsg("Advisory Reason updated");
			response.setStatus(HttpStatus.OK);
			advisoryReasonDetailRepository.save(ardNew);
		} else {
			arNew.setAdvisoryReasonName(advisoryReasonRequest.getAdvisoryReasonName().toUpperCase());
			advisoryReasonRepository.save(arNew);
			AdvisoryReasonDetail ardNew = new AdvisoryReasonDetail(
					new AdvisoryReasonDetailId (arNew, advisoryReasonRequest.getStartDate()), 
					advisoryService.get9999Date(),
					as.get()
					);
			response.setMsg("Advisory Reason created");
			response.setStatus(HttpStatus.CREATED);
			advisoryReasonDetailRepository.save(ardNew);
		}
		
		return response.getResponseEntity();
		
	}
	
	@GetMapping("private/get-advisory-reasons")
	public ResponseEntity<ApiResponseCustom> addAdvisoryReason(HttpServletRequest request){
		
		ResponseEntityHandler response = new ResponseEntityHandler(request);

		List<AdvisoryReason> list = advisoryReasonRepository.getAllValidAdvisoryReason();
		
		if(list.isEmpty()) {
			response.setMsg("No advisory reasons present");
			response.setStatus(HttpStatus.NOT_FOUND);
			return response.getResponseEntity();
		}
		
		response.setMsg(list);
		response.setStatus(HttpStatus.OK);
		return response.getResponseEntity();
		
	}
	
}
		
