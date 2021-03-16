package com.project.myblog.repository;

import com.project.myblog.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LanguageRepository extends JpaRepository<Language, String>{

	
}
