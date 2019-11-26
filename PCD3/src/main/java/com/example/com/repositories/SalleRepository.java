package com.example.com.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.com.entities.Salle;

public interface SalleRepository extends JpaRepository<Salle, Long>{
	
	//List<Salle>findByDispo(int dipso);
	List<Salle> findByStatus(String e);
	
	ArrayList<Salle> findAll();

}
