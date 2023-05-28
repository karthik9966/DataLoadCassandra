package com.ttu.edu.dlcassandra;

import com.ttu.edu.dlcassandra.connection.DataStaxAstraProperties;
import com.ttu.edu.dlcassandra.entity.Author;
import com.ttu.edu.dlcassandra.entity.Books;
import com.ttu.edu.dlcassandra.repository.AuthorRepository;
import com.ttu.edu.dlcassandra.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class DlcassandraApplication {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Value("${spring.datadump.author}")
	private String authorDump;

	@Value("${spring.datadump.works}")
	private String worksDump;

	public static void main(String[] args) {
		SpringApplication.run(DlcassandraApplication.class, args);
	}

	private void dataAuthorLoad()
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

	private void dataBooksLoad()
	{
		Path path = Paths.get(worksDump);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		try(Stream<String> lines = Files.lines(path))
		{
			lines.forEach(line -> {
				String jsonString = line.substring(line.indexOf("{"));
				JSONObject jsonObject = new JSONObject(jsonString);
				Books book = new Books();
				book.setId(jsonObject.getString("key").replace("/works/",""));
				book.setBook_name(jsonObject.optString("title"));
				JSONObject jsonDescriptionObject = jsonObject.optJSONObject("description");
				if(jsonDescriptionObject!=null)
				{
					book.setBook_description(jsonDescriptionObject.optString("value"));
				}
				JSONArray jsonCovers = jsonObject.optJSONArray("covers");
				List<Integer> covers = new ArrayList<>();
				if(jsonCovers!=null)
				{
					for(int i=0;i<jsonCovers.length();i++)
					{
						covers.add(jsonCovers.optInt(i));
					}
				}
				book.setCover_ids(covers);
				JSONObject jsonCreatedDateObject = jsonObject.optJSONObject("created");
				book.setPublished_date(LocalDate.parse(jsonCreatedDateObject.optString("value"),dateTimeFormatter));
				JSONArray authors = jsonObject.optJSONArray("authors");
				List<String> authorIds = new ArrayList<>();
				List<String> authorNames = new ArrayList<>();
				if(authors!=null)
				{
					for(int i=0;i<authors.length();i++)
					{
						authorIds.add(authors.getJSONObject(i).optJSONObject("author")
								.optString("key").replace("/authors/",""));
					}
					authorNames = authorIds.stream().map(id -> authorRepository.findById(id))
							.map(optionalAuthor -> {
								if (!optionalAuthor.isPresent()) return "Unknown author";
								return optionalAuthor.get().getName();
							}).collect(Collectors.toList());
				}
				book.setAuthor_id(authorIds);
				book.setAuthor_names(authorNames);
				System.out.println("Saving "+book);
				bookRepository.save(book);
			});
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	@PostConstruct
	public void initDatabase()
	{
		dataAuthorLoad();
		dataBooksLoad();
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties dataStaxAstraProperties)
	{
		Path bundle = dataStaxAstraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
