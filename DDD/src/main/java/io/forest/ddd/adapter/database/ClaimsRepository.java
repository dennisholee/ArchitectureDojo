package io.forest.ddd.adapter.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@org.jmolecules.ddd.annotation.Repository

@Repository
public interface ClaimsRepository extends JpaRepository<ClaimsEntity, String>{

}
