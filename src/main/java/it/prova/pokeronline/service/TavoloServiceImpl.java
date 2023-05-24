package it.prova.pokeronline.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.pokeronline.dto.AbbandonaTavoloDTO;
import it.prova.pokeronline.dto.AnalisiPartitaDTO;
import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Ruolo;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.tavolo.TavoloRepository;
import it.prova.pokeronline.web.api.exception.CreditoInsufficienteException;
import it.prova.pokeronline.web.api.exception.EsperienzaInsufficienteException;
import it.prova.pokeronline.web.api.exception.IdNonValidoException;
import it.prova.pokeronline.web.api.exception.ImpossibileGiocareConCreditoException;
import it.prova.pokeronline.web.api.exception.NessunTavoloLastGameException;
import it.prova.pokeronline.web.api.exception.NonPresenteAlTavoloException;
import it.prova.pokeronline.web.api.exception.OperazioneNegataException;

@Service
@Transactional
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
	@Transactional
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

	@Override
	public Page<Tavolo> findByExampleNativeWithPagination(Tavolo example, Integer pageNo, Integer pageSize,
			String sortBy) {
		return repository.findByExampleNativeWithPagination(example.getDenominazione(), example.getEsperienzaMinima(),
				example.getCifraMinima(), example.getDataCreazione(),
				PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
	}

	@Override
	@Transactional
	public TavoloDTO siediAlTavolo(Long idTavolo) {
		Tavolo tavolo = repository.findById(idTavolo).orElse(null);

		if (tavolo == null)
			throw new IdNonValidoException();

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);

		if (tavolo.getGiocatori().contains(utenteInSessione))
			throw new NonPresenteAlTavoloException();

		if (utenteInSessione.getCreditoAccumulato() < tavolo.getCifraMinima())
			throw new CreditoInsufficienteException();

		if (utenteInSessione.getEsperienzaAccumulata() < tavolo.getEsperienzaMinima())
			throw new EsperienzaInsufficienteException();

		tavolo.getGiocatori().add(utenteInSessione);

		return TavoloDTO.buildTavoloDTOFromModel(tavolo, true);

	}

	@Override
	public AnalisiPartitaDTO iniziaPartita(Long idTavolo) {
		Tavolo tavolo = repository.findById(idTavolo).orElse(null);

		if (tavolo == null)
			throw new IdNonValidoException();

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);

		if (!tavolo.getGiocatori().contains(utenteInSessione))
			throw new NonPresenteAlTavoloException();

		if (utenteInSessione.getCreditoAccumulato() == null || utenteInSessione.getCreditoAccumulato() == 0d) {
			throw new ImpossibileGiocareConCreditoException();
		}

		double segno = Math.random();
		if (segno < 0.5)
			segno = segno * -1;
		int somma = (int) (Math.random() * 500);
		int totale = (int) (segno * somma);

		Integer esperienzaGuadagnata = 0;

		if (totale > 0) {
			esperienzaGuadagnata += 5;
			if (totale > 200)
				esperienzaGuadagnata += 6;
			if (totale > 400)
				esperienzaGuadagnata += 7;
			if (totale > 499)
				esperienzaGuadagnata += 8;
		}

		if (totale <= 0) {
			esperienzaGuadagnata += 4;
			if (totale < -200)
				esperienzaGuadagnata += 3;
			if (totale < -400)
				esperienzaGuadagnata += 2;
			if (totale < -499)
				esperienzaGuadagnata += 1;
		}

		utenteInSessione.setEsperienzaAccumulata(esperienzaGuadagnata + utenteInSessione.getEsperienzaAccumulata());

		Double creditoDaInserire = utenteInSessione.getCreditoAccumulato() + totale;

		if (creditoDaInserire < 0) {
			creditoDaInserire = 0D;
		}

		utenteInSessione.setCreditoAccumulato(creditoDaInserire);

		AnalisiPartitaDTO result = new AnalisiPartitaDTO(totale, esperienzaGuadagnata);

		return result;
	}

	@Override
	public AbbandonaTavoloDTO abbandona(Long idTavolo) {
		Tavolo tavolo = repository.findById(idTavolo).orElse(null);

		if (tavolo == null)
			throw new IdNonValidoException();

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);

		if (!tavolo.getGiocatori().contains(utenteInSessione))
			throw new NonPresenteAlTavoloException();

		tavolo.getGiocatori().remove(utenteInSessione);

		AbbandonaTavoloDTO result = new AbbandonaTavoloDTO();

		result.setCredito(
				"Hai abbandonato il tavolo , il tuo credito attuale e'" + utenteInSessione.getCreditoAccumulato());
		result.setEsperienza(
				"Hai abbandonato il tavolo , la tua esperienza attuale e'" + utenteInSessione.getEsperienzaAccumulata());

		return result;
	}

	@Override
	public TavoloDTO lastGame() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteInSessione = utenteService.findByUsername(username);
		
		List<Tavolo> tavoliPresenti = (List<Tavolo>) repository.findAll();
		
		for (Tavolo tavoloItem : tavoliPresenti) {
			if(tavoloItem.getGiocatori().contains(utenteInSessione))
				return TavoloDTO.buildTavoloDTOFromModel(tavoloItem, true);
		}
		
		throw new NessunTavoloLastGameException();
	}

}
