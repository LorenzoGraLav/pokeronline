package it.prova.pokeronline.dto;

public class AnalisiPartitaDTO {
	private int risultato;
	private Integer esperienzaAccumulata;
	
	public AnalisiPartitaDTO() {
		
	}

	public AnalisiPartitaDTO(int risultato, Integer esperienzaAccumulata) {
		super();
		this.risultato = risultato;
		this.esperienzaAccumulata = esperienzaAccumulata;
	}

	public int getRisultato() {
		return risultato;
	}

	public void setRisultato(int risultato) {
		this.risultato = risultato;
	}

	public Integer getEsperienzaAccumulata() {
		return esperienzaAccumulata;
	}

	public void setEsperienzaAccumulata(Integer esperienzaAccumulata) {
		this.esperienzaAccumulata = esperienzaAccumulata;
	}
	
	
	
}
