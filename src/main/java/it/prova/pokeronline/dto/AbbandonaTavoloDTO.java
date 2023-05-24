package it.prova.pokeronline.dto;

public class AbbandonaTavoloDTO {
	private String credito;
	private String esperienza;
	
	public AbbandonaTavoloDTO() {
		
	}

	public AbbandonaTavoloDTO(String credito, String esperienza) {
		super();
		this.credito = credito;
		this.esperienza = esperienza;
	}

	public String getCredito() {
		return credito;
	}

	public void setCredito(String credito) {
		this.credito = credito;
	}

	public String getEsperienza() {
		return esperienza;
	}

	public void setEsperienza(String esperienza) {
		this.esperienza = esperienza;
	}
	
	
}
