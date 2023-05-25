package it.prova.pokeronline.dto;

import java.util.List;
import java.util.stream.Collectors;

public class LiberaTavoliDTO {
	String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LiberaTavoliDTO(String username) {
		super();
		this.username = username;
	}

	public LiberaTavoliDTO() {
		super();
	}
	
	public static List<String> createListStringToDTO(List<LiberaTavoliDTO> instance){
		return instance.stream().map(entity -> entity.getUsername()).collect(Collectors.toList());
	}
}
