package com.project.myblog.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "POST_COST")
@Data @AllArgsConstructor @NoArgsConstructor
public class PostCost {
	
	@EmbeddedId
	PostCostId postCostId;
	
	@Column(name = "END_DATE", nullable = false, columnDefinition = "DATE")
	private Date endDate;
	
	@Column(name = "COST", nullable = false, columnDefinition = "TINYINT(2)")
	private int cost;

}
