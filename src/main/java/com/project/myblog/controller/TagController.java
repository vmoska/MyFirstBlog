package com.project.myblog.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.project.myblog.entity.Tag;
import com.project.myblog.payload.response.ApiResponseCustom;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.PostRepository;
import com.project.myblog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class TagController {
	
	@Autowired
	TagRepository tagRepository;
	@Autowired
	PostRepository postRepository;
	
	@PostMapping("private/add-tag")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> addTag(
			HttpServletRequest request,
			@RequestParam @NotEmpty @NotBlank @Size(min=1, max=20) String tagName) {
		
		Object msg;
		HttpStatus status;
		ResponseEntityHandler response;
		
		Optional<Tag> tag = tagRepository.findById(tagName);
		
		if(tag.isEmpty()){
			tagRepository.save(new Tag(tagName.toUpperCase()));
			msg = "new tag added";
			
		} else {
			msg = "tag already present";
		}
		
		status = HttpStatus.OK;
		response = new ResponseEntityHandler(msg, request, status);
		return response.getResponseEntity();		
	}
	
	@DeleteMapping("private/delete-tag")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> deleteTag(HttpServletRequest request,
			@RequestParam @NotEmpty @NotBlank @Size(min=1, max=20) String tagName) {
		
		Object msg="Tag and his relation has been deleted";
		HttpStatus status;
		ResponseEntityHandler response;
		
		Optional<Tag> tag = tagRepository.findById(tagName);
		if(tag.isEmpty()){
			msg = "tag not found";
		}
		
		postRepository.deleteTagFromPost(tagName);
		tagRepository.delete(tag.get());
		
		status = HttpStatus.OK;
		response = new ResponseEntityHandler(msg, request, status);
		return response.getResponseEntity();
		
	}
	

}
