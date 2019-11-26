package com.example.com.entities;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;








@Entity
public class SeanceCours {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long seancecours_id ;
	
	
	@Convert(converter =  LocalDateConverter  .class)
	private LocalDateTime dateDebut ;
	@Convert(converter =  LocalDateConverter .class)
	private LocalDateTime dateFin;
	
	private String jour ;
	

	@JoinColumn(name="id")
	@ManyToOne(optional=false)
	private Enseignant enseignant ;
	
	
	@JoinColumn(name="matiere_id")
	@ManyToOne(optional=false)
	private Matiere matiere;
	
	
	@OneToOne
	@JoinColumn(name="salle_id")
	private Salle sal;
	
	
	@JoinTable(name="SEANCE_GROUPE", joinColumns= {@JoinColumn(name="seancecours_id")},inverseJoinColumns= {@JoinColumn(name="groupe_id")})
	@ManyToMany
	private List<Groupe> groupes ;


	public long getSeancecours_id() {
		return seancecours_id;
	}


	public void setSeancecours_id(long seancecours_id) {
		this.seancecours_id = seancecours_id;
	}


	public LocalDateTime getDateDebut() {
		return dateDebut;
	}


	public void setDateDebut(LocalDateTime dateDebut) {
		this.dateDebut = dateDebut;
	}


	public LocalDateTime getDateFin() {
		return dateFin;
	}


	public void setDateFin(LocalDateTime dateFin) {
		this.dateFin = dateFin;
	}


	public Enseignant getEnseignant() {
		return enseignant;
	}


	public void setEnseignant(Enseignant enseignant) {
		this.enseignant = enseignant;
	}


	public Matiere getMatiere() {
		return matiere;
	}


	public void setMatiere(Matiere matiere) {
		this.matiere = matiere;
	}


	public Salle getSal() {
		return sal;
	}


	public void setSal(Salle sal) {
		this.sal = sal;
	}


	public List<Groupe> getGroupes() {
		return groupes;
	}


	public void setGroupes(List<Groupe> groupes) {
		this.groupes = groupes;
	}


	public String getJour() {
		return jour;
	}


	public void setJour(String jour) {
		this.jour = jour;
	}
	
	
	
	

	

	

	
	

}
