package com.ttu.edu.dlcassandra.repository;

import com.ttu.edu.dlcassandra.entity.Author;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

public interface AuthorRepository extends CassandraRepository<Author,String> {

}
