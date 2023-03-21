package myspring.checkmime.model;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "checkmimeusers")
public class CheckmimeUser {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;

		@Column(name = "username")
		private String username;

		@Column(name = "password")
		private String password;

		@Column(name = "logged")
		private boolean logged;

	@Tolerate
		public CheckmimeUser() {}
}