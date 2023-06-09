package it.prova.pokeronline.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.pokeronline.dto.GestioneUtenteDTO;
import it.prova.pokeronline.model.StatoUtente;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.utente.UtenteRepository;

@Service
@Transactional
public class UtenteServiceImpl implements UtenteService {
	@Autowired
	private UtenteRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Utente> listAllUtenti() {
		return (List<Utente>) repository.findAll();
	}

	public Utente caricaSingoloUtente(Long id) {
		return repository.findById(id).orElse(null);
	}

	public Utente caricaSingoloUtenteConRuoli(Long id) {
		return repository.findByIdConRuoli(id).orElse(null);
	}

	@Transactional
	public Utente aggiorna(Utente utenteInstance) {
		// deve aggiornare solo nome, cognome, username, ruoli
		Utente utenteReloaded = repository.findById(utenteInstance.getId()).orElse(null);
		if (utenteReloaded == null)
			throw new RuntimeException("Elemento non trovato");
		utenteReloaded.setNome(utenteInstance.getNome());
		utenteReloaded.setCognome(utenteInstance.getCognome());
		utenteReloaded.setUsername(utenteInstance.getUsername());
		utenteReloaded.setCreditoAccumulato(utenteInstance.getCreditoAccumulato());
		utenteReloaded.setEsperienzaAccumulata(utenteInstance.getEsperienzaAccumulata());
		utenteReloaded.setRuoli(utenteInstance.getRuoli());
		return repository.save(utenteReloaded);
	}

	@Transactional
	public void inserisciNuovo(Utente utenteInstance) {
		utenteInstance.setStato(StatoUtente.CREATO);
		utenteInstance.setPassword(passwordEncoder.encode(utenteInstance.getPassword()));
		utenteInstance.setDataRegistrazione(LocalDate.now());
		repository.save(utenteInstance);
	}

	@Transactional
	public void rimuovi(Long idToRemove) {
		repository.deleteById(idToRemove);
		;
	}

	public Utente eseguiAccesso(String username, String password) {
		return repository.findByUsernameAndPasswordAndStato(username, password, StatoUtente.ATTIVO);
	}

//	public Utente findByUsernameAndPassword(String username, String password) {
//		return repository.findByUsernameAndPassword(username, password);
//	}

	@Transactional
	public void changeUserAbilitation(Long utenteInstanceId) {
		Utente utenteInstance = caricaSingoloUtente(utenteInstanceId);
		if (utenteInstance == null)
			throw new RuntimeException("Elemento non trovato.");

		if (utenteInstance.getStato() == null || utenteInstance.getStato().equals(StatoUtente.CREATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);
		else if (utenteInstance.getStato().equals(StatoUtente.ATTIVO))
			utenteInstance.setStato(StatoUtente.DISABILITATO);
		else if (utenteInstance.getStato().equals(StatoUtente.DISABILITATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);
	}

	public Utente findByUsername(String username) {
		return repository.findByUsername(username).orElse(null);
	}

	@Override
	public void aggiungiCredito(Long id, double creditoDaAggiungere) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = repository.findByUsername(username).orElse(null);

		utenteInSessione.setId(id);

		double cifraDaAggiungere = utenteInSessione.getCreditoAccumulato() + creditoDaAggiungere;

		utenteInSessione.setCreditoAccumulato(cifraDaAggiungere);

	}

	@Override
	@Transactional(readOnly = true)
	public List<GestioneUtenteDTO> findByExample(GestioneUtenteDTO example) {

		return GestioneUtenteDTO.buildGestioneUtenteDTOListFromModelList(
				repository.findByExample(example.buildGestioneUtenteModel(true)));
	}

	@Override
	@Transactional
	public GestioneUtenteDTO inserisciNuovo(GestioneUtenteDTO utenteInstance) {

		utenteInstance.setDataRegistrazione(LocalDate.now());
		utenteInstance.setStato(StatoUtente.ATTIVO);
		utenteInstance.setPassword(passwordEncoder.encode(utenteInstance.getPassword()));
		return GestioneUtenteDTO.buildGestioneUtenteDTOFromModel(repository.save(utenteInstance.buildGestioneUtenteModel(true)));
	}


}
