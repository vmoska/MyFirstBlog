package com.project.myblog.repository;

import java.util.Set;

import com.project.myblog.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CountryRepository extends JpaRepository<Country, String>{
	
	
	Set<Country> findByCountryCodeIn(Set<String> countriesCode);

}
