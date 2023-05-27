package com.ttu.edu.dlcassandra.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

@Table(value = "author_by_id")
public class Author {

    @Id @PrimaryKeyColumn(name = "author_id",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String id;
    @Column(value = "name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String name;

    public String getId(String key) {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
