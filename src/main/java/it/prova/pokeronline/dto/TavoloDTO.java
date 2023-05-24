package it.prova.pokeronline.dto;

import java.time.LocalDate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.prova.pokeronline.model.Tavolo;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TavoloDTO {

	private Long id;

	private Integer esperienzaMinima;

	private Double cifraMinima;

	@NotBlank(message = "{denominazione.notblank}")
	private String denominazione;

	private LocalDate dataCreazione;

	@JsonIgnoreProperties(value = { "tavoli" })
	private Set<UtenteDTO> giocatori = new HashSet<>(0);

	@JsonIgnoreProperties(value = { "tavolo" })
	private UtenteDTO utenteCreazione;

	public TavoloDTO() {

	}

	public TavoloDTO(Long id, Integer esperienzaMinima, Double cifraMinima,
			@NotBlank(message = "{denominazione.notblank}") String denominazione, LocalDate dataCreazione,
			Set<UtenteDTO> giocatori, UtenteDTO utenteCreazione) {
		super();
		this.id = id;
		this.esperienzaMinima = esperienzaMinima;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
		this.dataCreazione = dataCreazione;
		this.giocatori = giocatori;
		this.utenteCreazione = utenteCreazione;
	}

	public TavoloDTO(Integer esperienzaMinima, Double cifraMinima,
			@NotBlank(message = "{denominazione.notblank}") String denominazione, LocalDate dataCreazione,
			UtenteDTO utenteCreazione) {
		super();
		this.esperienzaMinima = esperienzaMinima;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
		this.dataCreazione = dataCreazione;
		this.utenteCreazione = utenteCreazione;
	}

	public TavoloDTO(Long id, Integer esperienzaMinima, Double cifraMinima,
			@NotBlank(message = "{denominazione.notblank}") String denominazione, LocalDate dataCreazione) {
		super();
		this.id = id;
		this.esperienzaMinima = esperienzaMinima;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
		this.dataCreazione = dataCreazione;
	}

	public TavoloDTO(Long id, Integer esperienzaMinima, Double cifraMinima,
			@NotBlank(message = "{denominazione.notblank}") String denominazione, LocalDate dataCreazione,
			UtenteDTO utenteCreazione) {
		super();
		this.id = id;
		this.esperienzaMinima = esperienzaMinima;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
		this.dataCreazione = dataCreazione;
		this.utenteCreazione = utenteCreazione;
	}

	public TavoloDTO(Long id, Integer esperienzaMinima, Double cifraMinima, String denominazione,
			UtenteDTO utenteCreazione) {
		this.id = id;
		this.esperienzaMinima =esperienzaMinima;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
		this.utenteCreazione = utenteCreazione;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getEsperienzaMinima() {
		return esperienzaMinima;
	}

	public void setEsperienzaMinima(Integer esperienzaMinima) {
		this.esperienzaMinima = esperienzaMinima;
	}

	public Double getCifraMinima() {
		return cifraMinima;
	}

	public void setCifraMinima(Double cifraMinima) {
		this.cifraMinima = cifraMinima;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public LocalDate getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Set<UtenteDTO> getGiocatori() {
		return giocatori;
	}

	public void setGiocatori(Set<UtenteDTO> giocatori) {
		this.giocatori = giocatori;
	}

	public UtenteDTO getUtenteCreazione() {
		return utenteCreazione;
	}

	public void setUtenteCreazione(UtenteDTO utenteCreazione) {
		this.utenteCreazione = utenteCreazione;
	}

	public Tavolo buildTavoloModel() {

		Tavolo result = new Tavolo(this.id, this.esperienzaMinima, this.cifraMinima, this.denominazione,
				this.dataCreazione);

		if (this.utenteCreazione == null)
			result.setUtenteCreazione(this.utenteCreazione.buildUtenteModel());

		return result;
	}

	public static TavoloDTO buildTavoloDTOFromModel(Tavolo tavoloModel,boolean includiGiocatori) {
		TavoloDTO result = new TavoloDTO(tavoloModel.getId(), tavoloModel.getEsperienzaMinima(),
				tavoloModel.getCifraMinima(), tavoloModel.getDenominazione(),UtenteDTO.buildUtenteDTOFromModel(tavoloModel.getUtenteCreazione()) );

		if(includiGiocatori)
		result.setGiocatori(UtenteDTO.createUtenteDTOSetFromModelSet(tavoloModel.getGiocatori()));
		return result;
	}
	public static List<TavoloDTO> createTavoloDTOListFromModelList(List<Tavolo> modelListInput, boolean includeUtentiGiocatori) {
		return modelListInput.stream().map(tavoloEntity -> {
			TavoloDTO result = TavoloDTO.buildTavoloDTOFromModel(tavoloEntity,includeUtentiGiocatori);
			if(includeUtentiGiocatori)
				result.setGiocatori(UtenteDTO.createUtenteDTOSetFromModelSet(tavoloEntity.getGiocatori()));
			return result;
		}).collect(Collectors.toList());
	}
	
	public static Page<TavoloDTO> fromModelPageToDTOPage(Page<Tavolo> input) {
		return new PageImpl<>(createTavoloDTOListFromModelList(input.getContent(), false),
				PageRequest.of(input.getPageable().getPageNumber(), input.getPageable().getPageSize(),
						input.getPageable().getSort()),
				input.getTotalElements());
	}
	
	
	

}
