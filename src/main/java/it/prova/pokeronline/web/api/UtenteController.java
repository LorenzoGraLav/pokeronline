package it.prova.pokeronline.web.api;

import java.util.List;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.GestioneUtenteDTO;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.security.dto.UtenteInfoJWTResponseDTO;
import it.prova.pokeronline.service.UtenteService;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;

@RestController
@RequestMapping("/api/utente")
public class UtenteController {
	@Autowired
	private UtenteService utenteService;

	// questa mi serve solo per capire se solo ADMIN vi ha accesso
	@GetMapping("/testSoloAdmin")
	public String test() {
		return "OK";
	}

	@GetMapping(value = "/userInfo")
	public ResponseEntity<UtenteInfoJWTResponseDTO> getUserInfo() {

		// se sono qui significa che sono autenticato quindi devo estrarre le info dal
		// contesto
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// estraggo le info dal principal
		Utente utenteLoggato = utenteService.findByUsername(username);
		List<String> ruoli = utenteLoggato.getRuoli().stream().map(item -> item.getCodice())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new UtenteInfoJWTResponseDTO(utenteLoggato.getNome(), utenteLoggato.getCognome(),
				utenteLoggato.getUsername(), ruoli));
	}

	@PostMapping("/{id}/acquistaCredito")
	public ResponseEntity<String> acquistaCredito(@PathVariable Long id, @RequestBody double creditoDaAggiungere) {
		utenteService.aggiungiCredito(id, creditoDaAggiungere);
		return ResponseEntity.ok("Credito aggiunto con successo");
	}
	
	
	@GetMapping
	public List<GestioneUtenteDTO> getAll() {
		return GestioneUtenteDTO.buildGestioneUtenteDTOListFromModelList(utenteService.listAllUtenti());
	}

	@GetMapping("/{id}")
	public GestioneUtenteDTO findById(@PathVariable(value = "id", required = true) long id) {

		Utente utente = utenteService.caricaSingoloUtenteConRuoli(id);

		if (utente == null)
			throw new IdNotNullForInsertException("");

		return GestioneUtenteDTO.buildGestioneUtenteDTOFromModel(utente);

	}

	@PutMapping("/{id}")
	public GestioneUtenteDTO update(@Valid @RequestBody GestioneUtenteDTO utenteInput,
			@PathVariable(required = true) Long id) {

		Utente utente = utenteService.caricaSingoloUtente(id);

		if (utente == null)
			throw new IdNotNullForInsertException("");

		utenteInput.setId(id);
		Utente utenteAggiornato = utenteService.aggiorna(utenteInput.buildGestioneUtenteModel(false));
		return GestioneUtenteDTO.buildGestioneUtenteDTOFromModel(utenteAggiornato);

	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(required = true) Long id) {

		Utente utente = utenteService.caricaSingoloUtente(id);

		if (utente == null)
			throw new IdNotNullForInsertException("");
		utenteService.rimuovi(id);

	}
	
	@PostMapping("/seacrh")
	public List<GestioneUtenteDTO> findByExample(@RequestBody GestioneUtenteDTO example) {
		return utenteService.findByExample(example);
	}

}
