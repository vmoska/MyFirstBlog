package com.project.myblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ADVISORY_REASON_DETAIL")
@Data @AllArgsConstructor @NoArgsConstructor
public class AdvisoryReasonDetail {
	
	@EmbeddedId
	AdvisoryReasonDetailId advisoryReasonDetailId;
	
	@Column(name="END_DATE", nullable=false, columnDefinition="DATE")
	private Date endDate;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ADVISORY_SEVERITY", nullable=false)
	private AdvisorySeverity advisorySeverity;

		
	

}
