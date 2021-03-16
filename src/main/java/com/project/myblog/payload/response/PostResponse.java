package com.project.myblog.payload.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PostResponse {
	
	private long id;
	private String title;
	private String overview;
	private Date updatedAt;
	private String author;
	private String langName;
	private double average;
	
}
