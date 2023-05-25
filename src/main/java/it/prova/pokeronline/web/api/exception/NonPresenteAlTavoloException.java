package it.prova.pokeronline.web.api.exception;

public class NonPresenteAlTavoloException extends RuntimeException {


	private static final long serialVersionUID = 1L;
	
	public NonPresenteAlTavoloException(String message) {
		super(message);
	}

}
