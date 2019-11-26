package com.example.com.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.swing.text.Utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.com.entities.Absence;
import com.example.com.entities.Enseignant;
import com.example.com.entities.Groupe;
import com.example.com.entities.Rattrapage;
import com.example.com.entities.Role;
import com.example.com.entities.Salle;
import com.example.com.entities.SeanceCours;
import com.example.com.entities.User;
import com.example.com.entities.Etudiant;
import com.example.com.repositories.EnseignantRepository;
import com.example.com.repositories.EtudiantRepository;
import com.example.com.repositories.RattrapageRepository;
import com.example.com.repositories.SalleRepository;
import com.example.com.repositories.UserRepository;
import com.example.com.services.AbsenceService;
import com.example.com.services.EmailService;
import com.example.com.services.GroupeService;
import com.example.com.services.Mail;
import com.example.com.services.SalleService;
import com.example.com.services.SeanceCoursService;
import com.example.com.services.UserService;


import freemarker.template.TemplateException;

@Controller
public class EnseignantController {

	
	
	@Autowired
	private com.example.com.services.Utilitaire utility ;
	@Autowired
	private AbsenceService absenceService;

	@Autowired
	private EtudiantRepository etudiantRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private EnseignantRepository enseignantRepository;

	@Autowired
	private SeanceCoursService seancecoursService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private RattrapageRepository rattrapageRepository;
	@Autowired
	private GroupeService groupeService;
     @Autowired
     private SalleRepository salleRepository ;
	
	 @Autowired
	 private UserRepository userRepository ;
//	@Autowired
//	private SalleService salleService;

	@GetMapping("/enseignant/contact")
	public String returnContact(Model model) {
		return "/enseignant/contact";
	}

	@GetMapping("/enseignant/apropos")
	public String returnApropos(Model model) {
		return "/enseignant/apropos";
	}
	/*
	 * @GetMapping("/enseignant/espace_enseignant") public String
	 * returnAcceuilEnseignant(Model model) { return
	 * "/enseignant/espace_enseignant"; }
	 */

	@GetMapping("/enseignant/devoirsurveille")
	public String returnDSEnseignant(Model model) {
		return "/enseignant/devoirsurveille";
	}

	@GetMapping("/enseignant/emploiEnseignant")
	public String returnEmploiEnseignant(Model model) {
		return "/enseignant/emploiEnseignant";
	}

	@GetMapping("/enseignant/absence")
	public String welcome(Model model) {

		model.addAttribute("AbsenceForm", new Absence());

		return "/enseignant/absence";

	}

	@GetMapping("/enseignant/absenceSuccess")
	public String absenceSuccess(Model model) {

		model.addAttribute("AbsenceForm", new Absence());

		ArrayList<Integer> result = (ArrayList<Integer>) absenceService.findEnseignantByGroupe("II-1-A");
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			BigInteger integer = (BigInteger) iterator.next();

			System.out.println(enseignantRepository.findById(integer.longValueExact()).get().getPrenom());
		}

		return "/enseignant/absenceSuccess";
	}

	@PostMapping("/enseignant/absence")
	public String inscription(@ModelAttribute("absenceForm") Absence aForm, Model model,
			RedirectAttributes attributes) {
		// identification du user connecte par son email
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		Optional<Enseignant> e1 = enseignantRepository.findById(user.getId());

		aForm.setDateDebut(aForm.getDateDebut());
		aForm.setDateFin(aForm.getDateFin());
		aForm.setRaison(aForm.getRaison());
		aForm.setEnseignant(e1.get());
		absenceService.save(aForm);
		attributes.addFlashAttribute("flashAttribute", "Your Request is accepted by the System");
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Adding New Absence Request To Our ENSI scheduler !!! ");

		Set<Groupe> groupes = e1.get().getGroupes();
		Mail mail = new Mail();
		mail.setFrom("ulquiorra.cifer19924@gmail.com");
		mail.setSubject("Message d'absence");

		Map<String, String> modelEmail = new HashMap();

		modelEmail.put("debut", aForm.getDateDebut().format(DateTimeFormatter.ISO_DATE).toString());
		modelEmail.put("fin", aForm.getDateDebut().format(DateTimeFormatter.ISO_DATE).toString());
		modelEmail.put("enseignant", user.getNom());
		for (Groupe groupe : groupes) {

			modelEmail.put("groupe", groupe.getGroupeN());

			mail.setModel(modelEmail);
			String[] array = etudiantRepository.findByGroupeEmails(groupe.getGroupeN()).stream()
					.peek(System.out::println).toArray(String[]::new);
			try {
				emailService.sendSimpleMessage(mail, array);
			} catch (MessagingException | IOException | TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// invoke service notifier
		}

		// not redirecting
		return "redirect:/enseignant/espace_enseignant";
	}

	@GetMapping("/enseignant/espace_enseignant")
	public String welcomeEtudiant(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		java.util.Set<Role> set = user.getRoles();
		set.stream().map(r -> r.getRoleName()).findAny().ifPresent(System.out::println);
		Logger.getLogger(getClass().getName()).log(Level.INFO,
				"Current User: " + user.getNom() + " " + " Name: " + user.getPrenom() + " (" + user.getEmail() + ")");

		return "enseignant/espace_enseignant";
	}

	@GetMapping("/enseignant/premiere")
	public String Premiere(Model model) {

		model.addAttribute("AbsenceForm", new Absence());

		return "/enseignant/premiere";
	}

	@GetMapping("/enseignant/deuxieme")
	public String Deuxieme(Model model) {

		model.addAttribute("AbsenceForm", new Absence());

		return "/enseignant/deuxieme";
	}

	@GetMapping("/enseignant/troisieme")
	public String Troisieme(Model model) {

		model.addAttribute("AbsenceForm", new Absence());

		return "/enseignant/troisieme";
	}

	/*----------------Rattrapage ------------------------ */
	@GetMapping("/enseignant/rattrapage")
	public String GetRattrapage(Model model) {

		List<Groupe> listgroupes = groupeService.findAll();
		List<String> groupes = new ArrayList<>();
		for (Groupe groupe : listgroupes) {

			groupes.add(groupe.getGroupeN());
		}
		model.addAttribute("groupes", groupes);
		model.addAttribute("RattrapageForm", new Rattrapage());

		return "/enseignant/rattrapage";
	}

	@PostMapping("/enseignant/rattrapage")
	public String postRattrapage(@ModelAttribute("rattrapageForm") Rattrapage rattrapage, Model model,
			RedirectAttributes attributes) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		User user = userService.findUserByEmail(auth.getName());
		Optional<Enseignant> e1 = enseignantRepository.findById(user.getId());
		Groupe groupe = rattrapage.getGroupe();
		String groupeName = groupe.getGroupeN();
		System.out.println("+++++++++"+ groupeName + "+++++++++++++++++++");
		
		long id = groupe.getId();
		System.out.println("+++++++++++++" + id + "++++++++++++");
		
		List<SeanceCours> seances = seancecoursService.findByGroupes(id);
		if(utility.possiblity(rattrapage ,seances ) == true) {
			/*----- rattrapage possible ---- */
			Groupe grp = new Groupe();
			grp.setGroupeN(groupeName);
			rattrapage.setDate_debut(rattrapage.getDate_debut());
			rattrapage.setDate_debut(rattrapage.getDate_fin());
			rattrapage.setGroupe(grp);
			rattrapage.setEnseignant(e1.get());
			rattrapage.setStatus(Rattrapage.STATUS.CONFIRME);
			rattrapageRepository.save(rattrapage);
		}
			
	return "redirect:/enseignant/espace_enseignant";

}
	
//}else {
//	/*---- redirecting to  suggestions page ----- */
//
//		return "redirect:/enseignant/enseignant_suggestions";
//	}
	
	
	
	
	/*----------------Suggestions enseignant -------- */
	
	@GetMapping("/enseignant/enseignant_suggestions")
	public String GetSuggestions(@ModelAttribute("rattrapageForm") Rattrapage rattrapage,Model model) {

		List<String> suggestions = utility.GetSuggestions(rattrapage,rattrapage.getGroupe());
		model.addAttribute("dates", suggestions);
		
		
		

		return "/enseignant/enseignant_suggestions";
	}
	
//	@PostMapping("/enseignant/enseignant_suggestions")
//	public String postSuggestions(@ModelAttribute("SeancesRattrapage") List<SeanceCours> seances, Model model) {
//
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String email = auth.getName();
//		User user = userService.findUserByEmail(auth.getName());
//		Optional<Enseignant> e1 = enseignantRepository.findById(user.getId());
//		
//		
//		// je l ai fais sans sondage
//		
//		// obtenir la seance choisi
//		SeanceCours seance ;
//		
//		Rattrapage rattrapage = new Rattrapage();
//		rattrapage.setDate_debut(seance.getDateDebut());
//		rattrapage.setDate_debut(seance.getDateFin());
//		rattrapage.setSalle(seance.getSal());
//		rattrapageRepository.save(rattrapage);
//		
//		/*------ envoi d un mail aux deux parts ---- */
//		
//		
//		Mail mail = new Mail();
//		mail.setFrom("admin@gmail.com");
//		mail.setSubject("Email de Rattrapage ");
//
//		Map<String, String> modelEmail = new HashMap();
//
//		modelEmail.put("debut", seances.getDateDebut().format(DateTimeFormatter.ISO_DATE).toString());
//		modelEmail.put("fin", aForm.getDateDebut().format(DateTimeFormatter.ISO_DATE).toString());
//		modelEmail.put("enseignant", user.getNom());
//		for (Groupe groupe : groupes) {
//
//			modelEmail.put("groupe", groupe.getGroupeN());
//
//			mail.setModel(modelEmail);
//			String[] array = etudiantRepository.findByGroupeEmails(groupe.getGroupeN()).stream()
//					.peek(System.out::println).toArray(String[]::new);
//			try {
//				emailService.sendSimpleMessage(mail, array);
//			} catch (MessagingException | IOException | TemplateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			// invoke service notifier
//		}
//
//		// not redirecting
//		return "redirect:/enseignant/espace_enseignant";
//
//		
//	}
//	

	/*----------------Profil Enseignant -------------------- */
	@GetMapping("/enseignant/profilEnseignant")
	public String GetProfil(Model model, User user) {

		model.addAttribute("ProfilForm", new User());

		return "/enseignant/profilEnseignant";
	}

	@PostMapping("/enseignant/profilEnseignant")

	public String postProfil(@ModelAttribute("profilForm") User user, Model model, RedirectAttributes attributes) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentuser = userService.findUserByEmail(auth.getName());
       long id = currentuser.getId();
		currentuser.setPrenom(user.getPrenom());
		currentuser.setNom(user.getNom());
		currentuser.setEmail(user.getEmail());
		currentuser.setPassword(user.getPassword());

		userRepository.save(currentuser);

		attributes.addFlashAttribute("flashAttribute", "Your Request is accepted by the System");
		Logger.getLogger(getClass().getName()).log(Level.INFO, "setting user infos !!! ");

		return "redirect:/enseignant/espace_enseignant";
	}

}
