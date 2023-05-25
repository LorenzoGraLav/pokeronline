package it.prova.pokeronline.web.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.AbbandonaTavoloDTO;
import it.prova.pokeronline.dto.AnalisiPartitaDTO;
import it.prova.pokeronline.dto.LiberaTavoliDTO;
import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.service.TavoloService;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;
import it.prova.pokeronline.web.api.exception.TavoloNotFoundException;

@RestController
@RequestMapping("/api/tavolo")
public class TavoloController {
	@Autowired
	private TavoloService tavoloService;

	@GetMapping
	public List<TavoloDTO> getAll() {
		return TavoloDTO.createTavoloDTOListFromModelList(tavoloService.tavoliByUtente(), true);
	}

	@GetMapping("/{id}")
	public TavoloDTO findById(@PathVariable(value = "id", required = true) long id) {

		Tavolo tavolo = tavoloService.caricaSingoloElemento(id);

		if (tavolo == null) {
			throw new TavoloNotFoundException("Tavolo not found con id: " + id);
		}

		return TavoloDTO.buildTavoloDTOFromModel(tavolo, true);

	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TavoloDTO createNew(@Valid @RequestBody TavoloDTO tavoloInput) {
		if (tavoloInput.getId() != null) {
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		}

		Tavolo tavoloInserito = tavoloService.inserisciNuovo(tavoloInput.buildTavoloModel());

		return TavoloDTO.buildTavoloDTOFromModel(tavoloInserito, true);
	}

	@PutMapping("/{id}")
	public TavoloDTO update(@Valid @RequestBody TavoloDTO tavoloInput, @PathVariable(required = true) Long id) {
		Tavolo tavolo = tavoloService.caricaSingoloElemento(id);

		if (tavolo == null) {
			throw new TavoloNotFoundException("Tavolo not found con id " + id);
		}

		tavoloInput.setId(id);

		Tavolo tavoloAggiornato = tavoloService.aggiorna(tavoloInput.buildTavoloModel());

		return TavoloDTO.buildTavoloDTOFromModel(tavoloAggiornato, true);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(required = true) Long id) {
		tavoloService.rimuovi(id);
	}

	@PostMapping("/search")
	public List<TavoloDTO> search(@RequestBody TavoloDTO example) {
		return TavoloDTO.createTavoloDTOListFromModelList(tavoloService.findByExample(example.buildTavoloModel()), true);
	}

	@PostMapping("/searchNativeWithPagination")
	public ResponseEntity<Page<TavoloDTO>> searchNativePaginated(@RequestBody TavoloDTO example,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "0") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortBy) {

		Page<Tavolo> entityPageResults = tavoloService.findByExampleNativeWithPagination(example.buildTavoloModel(),
				pageNo, pageSize, sortBy);

		return new ResponseEntity<Page<TavoloDTO>>(TavoloDTO.fromModelPageToDTOPage(entityPageResults), HttpStatus.OK);
	}
	
	
	
	@GetMapping("/siediNelTavolo/{id}")
	public TavoloDTO siediNelTavolo(@PathVariable(required = true) Long id) {
		return tavoloService.siediAlTavolo(id);
	}

	@PostMapping("/iniziaPartita/{id}")
	public AnalisiPartitaDTO giocaNelTavolo(@PathVariable(required = true) Long id) {

		return tavoloService.iniziaPartita(id);

	}

	@GetMapping("/abbandonaIlTavolo/{id}")
	public AbbandonaTavoloDTO abbandonaIlTavolo(@PathVariable(required = true) Long id) {
		return tavoloService.abbandona(id);
	}

	@GetMapping("/lastGame")
	public TavoloDTO lastGame() {
		return tavoloService.lastGame();
	}
	
	@GetMapping("/soglia/{soglia}")
	public List<TavoloDTO> estraiTavoliConUnUtenteAlDiSopraDiSoglia(@PathVariable Integer soglia) {
		
	    return tavoloService.estraiTavoliConUnUtenteAlDiSopraDiSoglia(soglia);
	    
	
	}
	
	@GetMapping("/admin/trovaTavoloConMassimaEsperienzaGiocatori")
	public TavoloDTO trovaTavoloConMassimaEsperienzaGiocatori() {
		return tavoloService.trovaTavoloConEsperienzaMassima();
	}
	
	
	@PostMapping("/admin/svuotaTavoli")
	public String liberaTavoli(@RequestBody List<LiberaTavoliDTO>tavoli) {
		
		tavoloService.liberaTavoloDaUtenti(tavoli);
		
		return "operazione eseguita";
		
	}
	

}
