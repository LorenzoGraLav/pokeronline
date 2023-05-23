package it.prova.pokeronline.service;

import java.time.LocalDate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.pokeronline.model.Ruolo;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.tavolo.TavoloRepository;
import it.prova.pokeronline.web.api.exception.OperazioneNegataException;

@Service
@Transactional(readOnly = true)
public class TavoloServiceImpl implements TavoloService {
	@Autowired
	private TavoloRepository repository;

	@Autowired
	private UtenteService utenteService;

	@Override
	public List<Tavolo> listAllElements(boolean eager) {
		if (eager)
			return (List<Tavolo>) repository.findAllTavoloEager();

		return (List<Tavolo>) repository.findAll();
	}

	@Override
	public Tavolo caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Tavolo caricaSingoloElementoEager(Long id) {
		return repository.findSingleTavoloEager(id);
	}

	@Override
	@Transactional
	public Tavolo aggiorna(Tavolo tavoloInstance) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);
		tavoloInstance.setUtenteCreazione(utenteInSessione);
		return repository.save(tavoloInstance);
	}

	@Override
	@Transactional
	public Tavolo inserisciNuovo(Tavolo tavoloInstance) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);
		tavoloInstance.setUtenteCreazione(utenteInSessione);
		return repository.save(tavoloInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Long idToRemove) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);
		Tavolo tavoloDaRimuovere = repository.findSingleTavoloEager(idToRemove);
		if (tavoloDaRimuovere.getUtenteCreazione().getId() != utenteInSessione.getId()) {
			throw new OperazioneNegataException("l'agenda non e' associata a questo utente, non puo' essere rimossa");
		}
		repository.deleteById(idToRemove);
	}

	@Override
	public List<Tavolo> findByExample(Tavolo example) {

		return repository.findByExample(example);
	}

	@Override
	public List<Tavolo> findByDenominazione(String denominazione) {
		return repository.findByDenominazione(denominazione);
	}

	@Override
	public List<Tavolo> tavoliByUtente() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);
		for (Ruolo ruoloItem : utenteInSessione.getRuoli())
			if (ruoloItem.getCodice().equals("ROLE_ADMIN"))
				return (List<Tavolo>) repository.findAllTavoloEager();
		return (List<Tavolo>) repository.tavoliUtente(utenteInSessione.getId());
	}

	@Override
	public void firstInsert() {
		Tavolo tavoloAdmin = new Tavolo(50, 1000.0, "tavoloAdmin", LocalDate.now());

		tavoloAdmin.setUtenteCreazione(utenteService.findByUsername("admin"));
		if (findByDenominazione(tavoloAdmin.getDenominazione()).isEmpty()) {
			repository.save(tavoloAdmin);
		}

		Tavolo tavoloGiocatore1 = new Tavolo(30, 800.0, "tavoloGiocatore1", LocalDate.now());

		tavoloGiocatore1.setUtenteCreazione(utenteService.findByUsername("user"));
		if (findByDenominazione(tavoloGiocatore1.getDenominazione()).isEmpty()) {
			repository.save(tavoloGiocatore1);
		}

	}

}
