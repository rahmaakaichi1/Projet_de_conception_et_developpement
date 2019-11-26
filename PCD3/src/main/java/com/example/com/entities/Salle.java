package com.example.com.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.beans.factory.DisposableBean;

import antlr.collections.List;
import ch.qos.logback.core.status.Status;

@Entity
public class Salle {
	
	@Id
	@GeneratedValue
	private  long id_salle;
	
	private String type;
	
	private String status;
	
	@OneToOne(mappedBy="salle")
	private Rattrapage rattrapage ;
	
	
	
	@OneToOne(mappedBy="sal")
	private SeanceCours sCours;
	
	
	public long getId_salle() {
		return id_salle;
	}

	public void setId_salle(long id_salle) {
		this.id_salle = id_salle;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Rattrapage getRattrapage() {
		return rattrapage;
	}

	public void setRattrapage(Rattrapage rattrapage) {
		this.rattrapage = rattrapage;
	}

	
	
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SeanceCours getsCours() {
		return sCours;
	}

	public void setsCours(SeanceCours sCours) {
		this.sCours = sCours;
	}

	
	
}
