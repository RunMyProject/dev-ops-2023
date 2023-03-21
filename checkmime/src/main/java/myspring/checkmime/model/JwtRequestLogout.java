package myspring.checkmime.model;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import java.io.Serializable;

public class JwtRequestLogout implements Serializable {

	private static final long serialVersionUID = 7776468583005150707L;

	private String username;

	//default constructor for JSON Parsing
	public JwtRequestLogout()
	{
	}

	public JwtRequestLogout(String username) {
		this.setUsername(username);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}