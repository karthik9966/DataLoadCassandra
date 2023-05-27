package com.ttu.edu.dlcassandra;

import com.ttu.edu.dlcassandra.connection.DataStaxAstraProperties;
import com.ttu.edu.dlcassandra.entity.Author;
import com.ttu.edu.dlcassandra.repository.AuthorRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class DlcassandraApplication {

	@Autowired
	private AuthorRepository authorRepository;

	@Value("${spring.datadump.author}")
	private String authorDump;

	@Value("${spring.datadump.works}")
	private String worksDump;

	public static void main(String[] args) {
		SpringApplication.run(DlcassandraApplication.class, args);
	}

	@PostConstruct
	public void dataLoadFromFile()
	{
		Path path = Paths.get(authorDump);
		try(Stream<String> lines = Files.lines(path))
		{
			lines.forEach(line -> {
				String jsonString = line.substring(line.indexOf("{"));
				JSONObject jsonObject = new JSONObject(jsonString);
				Author author = new Author();
				author.setName(jsonObject.optString("name"));
				author.setId(jsonObject.optString("key").replace("/authors/",""));
				authorRepository.save(author);
			});
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}

	}
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties dataStaxAstraProperties)
	{
		Path bundle = dataStaxAstraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
