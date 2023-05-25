package it.prova.pokeronline.service;

import java.util.List;

import it.prova.pokeronline.dto.GestioneUtenteDTO;
import it.prova.pokeronline.model.Utente;

public interface UtenteService {
	public List<Utente> listAllUtenti();

	public Utente caricaSingoloUtente(Long id);

	public Utente caricaSingoloUtenteConRuoli(Long id);

	public Utente aggiorna(Utente utenteInstance);

	public void inserisciNuovo(Utente utenteInstance);

	public void rimuovi(Long idToRemove);

	public List<GestioneUtenteDTO> findByExample(GestioneUtenteDTO example);

	// public Utente findByUsernameAndPassword(String username, String password);

	public Utente eseguiAccesso(String username, String password);

	public void changeUserAbilitation(Long utenteInstanceId);

	public Utente findByUsername(String username);

	public void aggiungiCredito(Long id, double creditoDaAggiungere);
	
	//public GestioneUtenteDTO abilita(Long idDaAbilitare);
}
