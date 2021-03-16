package com.project.myblog.entity;

import com.project.myblog.entity.audit.DateAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="POST")
@Getter @Setter @NoArgsConstructor
public class Post extends DateAudit {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, unique=true, length=100)
	private String title;
	
	@Column(nullable=false, columnDefinition="TEXT")
	private String content;

	@Column(name="IS_APPROVED", nullable=false, columnDefinition="TINYINT(1)")
	private Boolean approved = false;
	
	@Column(name="IS_PUBLISHED", nullable=false, columnDefinition="TINYINT(1)")
	private Boolean published = false;
	
	@Column(nullable=false)
	private String overview;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AUTHOR", nullable=false)
	private User author;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LANGUAGE", nullable=false)
	private Language language;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="POST_COUNTRIES", 
		joinColumns = {@JoinColumn(name="POST_ID", referencedColumnName="ID")},
		inverseJoinColumns = {@JoinColumn(name="COUNTRY_ID", referencedColumnName="COUNTRY_CODE")})
	Set<Country> countries;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="POST_TAGS", 
		joinColumns = {@JoinColumn(name="POST_ID", referencedColumnName="ID")},
		inverseJoinColumns = {@JoinColumn(name="TAG_ID", referencedColumnName="TAG_NAME")})
	Set<Tag> tags;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "POST_IMAGE")
	private DbFile postImage;
	

	public Post(String title, String overview, String content, User author, Language language) {
		super();
		this.title = title;
		this.overview = overview;
		this.content = content;
		this.author = author;
		this.language = language;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Post))
			return false;
		Post other = (Post) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
