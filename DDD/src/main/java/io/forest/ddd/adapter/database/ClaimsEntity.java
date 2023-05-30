package io.forest.ddd.adapter.database;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ClaimsEntity {

	@Id
	UUID id;

}
