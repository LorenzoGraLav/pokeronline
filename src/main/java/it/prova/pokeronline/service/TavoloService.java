package it.prova.pokeronline.service;

import java.util.List;

import org.springframework.data.domain.Page;

import it.prova.pokeronline.dto.AbbandonaTavoloDTO;
import it.prova.pokeronline.dto.AnalisiPartitaDTO;
import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Tavolo;

public interface TavoloService {

	List<Tavolo> listAllElements(boolean eager);

	Tavolo caricaSingoloElemento(Long id);

	Tavolo caricaSingoloElementoEager(Long id);

	Tavolo aggiorna(Tavolo tavoloInstance);

	Tavolo inserisciNuovo(Tavolo tavoloInstance);

	void rimuovi(Long idToRemove);

	List<Tavolo> findByExample(Tavolo example);

	List<Tavolo> findByDenominazione(String denominazione);

	List<Tavolo> tavoliByUtente();

	void firstInsert();

	Page<Tavolo> findByExampleNativeWithPagination(Tavolo example, Integer pageNo, Integer pageSize, String sortBy);

    public TavoloDTO siediAlTavolo(Long idTavolo);
	
	public AnalisiPartitaDTO iniziaPartita(Long idTavolo);
	
	public AbbandonaTavoloDTO abbandona(Long idTavolo);
	
	public TavoloDTO lastGame();
	
	List<TavoloDTO> estraiTavoliConUnUtenteAlDiSopraDiSoglia(Integer soglia);
	
	public TavoloDTO trovaTavoloConEsperienzaMassima();
	

}
