package com.ttu.edu.dlcassandra.repository;

import com.ttu.edu.dlcassandra.entity.Books;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CassandraRepository<Books,String> {
}
