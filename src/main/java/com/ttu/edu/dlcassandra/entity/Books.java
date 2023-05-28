package com.ttu.edu.dlcassandra.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.LocalDate;
import java.util.List;

@Table(value = "books_by_id")
public class Books {

    @Id @PrimaryKeyColumn(name = "book_id",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
    private String id;

    @Column("author_id")
    @CassandraType(type = CassandraType.Name.LIST,typeArguments = CassandraType.Name.TEXT)
    private List<String> author_id;

    @Column("author_names")
    @CassandraType(type = CassandraType.Name.LIST,typeArguments = CassandraType.Name.TEXT)
    private List<String> author_names;

    @Column("book_description")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String book_description;

    @Column("book_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String book_name;

    @Column("published_date")
    @CassandraType(type = CassandraType.Name.DATE)
    private LocalDate published_date;

    @Column("cover_ids")
    @CassandraType(type = CassandraType.Name.LIST,typeArguments = CassandraType.Name.INT)
    private List<Integer> cover_ids;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(List<String> author_id) {
        this.author_id = author_id;
    }

    public List<String> getAuthor_names() {
        return author_names;
    }

    public void setAuthor_names(List<String> author_names) {
        this.author_names = author_names;
    }

    public String getBook_description() {
        return book_description;
    }

    public void setBook_description(String book_description) {
        this.book_description = book_description;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public LocalDate getPublished_date() {
        return published_date;
    }

    public void setPublished_date(LocalDate published_date) {
        this.published_date = published_date;
    }

    public List<Integer> getCover_ids() {
        return cover_ids;
    }

    public void setCover_ids(List<Integer> cover_ids) {
        this.cover_ids = cover_ids;
    }
}
