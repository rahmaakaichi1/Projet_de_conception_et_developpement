package com.example.com.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.com.entities.SeanceCours;


public interface SeanceCoursRepository extends JpaRepository<SeanceCours, Long>{
	
	List<SeanceCours> findByGroupes(long id);

}
