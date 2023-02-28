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

import javax.persistence.*;

@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "checkmimes")
public class Checkmime {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private long id;

		@Column(name = "format")
		private String format;

		@Column(name = "description")
		private String description;

		@Column(name = "enabled")
		private boolean enabled;

		@Tolerate
		public Checkmime() {}
}