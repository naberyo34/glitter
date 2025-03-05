package com.example.demo;

import java.util.List;
import com.example.demo.domain.Task;
import com.example.demo.mapper.CustomTaskMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	private final CustomTaskMapper taskMapper;

	// コンストラクターで Mapper をインジェクション
	public DemoApplication(CustomTaskMapper taskMapper) {
		this.taskMapper = taskMapper;
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {
		List<Task> tasks = taskMapper.selectAll();
		for (Task task : tasks) {
			System.out.println(task.getValue());
		}
	}
}
