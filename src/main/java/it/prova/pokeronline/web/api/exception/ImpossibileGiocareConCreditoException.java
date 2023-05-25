package it.prova.pokeronline.web.api.exception;

public class ImpossibileGiocareConCreditoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ImpossibileGiocareConCreditoException(String message) {
		super(message);
	}

}
