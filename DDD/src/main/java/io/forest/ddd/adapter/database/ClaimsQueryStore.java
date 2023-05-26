package io.forest.ddd.adapter.database;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import io.forest.ddd.application.dto.ClaimsDTO;

@Repository
public class ClaimsQueryStore {

	private MongoTemplate mongoTemplate;

	public ClaimsQueryStore(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public Optional<ClaimsDTO> findById(String id) {
		ClaimsDTO claimsDTO = this.mongoTemplate.findById(new ObjectId(id), ClaimsDTO.class);
		return Optional.ofNullable(claimsDTO);
	}

	public List<ClaimsDTO> findByClaimType(String claimType) {
		Query query = new Query();
		query.addCriteria(Criteria.where("claimType")
				.is(claimType));

		return this.mongoTemplate.findAll(ClaimsDTO.class);
	}
}
