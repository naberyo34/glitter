package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	User[] users = {
			new User("たま", "年中パーカー着てます"),
			new User("りん", "眼鏡変えました")
	};

	@RequestMapping("/user/{number}")
	public User user(@PathVariable int number) {
		if (number < 0 || number >= users.length) {
			return null;
		}
		return users[number];
	}

	class User {
		private String username;
		private String profile;

		public User(String username, String profile) {
			super();
			this.username = username;
			this.profile = profile;
		}

		public String getProfile() {
			return profile;
		}

		public String getUsername() {
			return username;
		}
	}
}
