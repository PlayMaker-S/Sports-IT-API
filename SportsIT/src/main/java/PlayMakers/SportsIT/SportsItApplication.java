package PlayMakers.SportsIT;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SportsItApplication {

	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}

	public static void main(String[] args) {
		SpringApplication.run(SportsItApplication.class, args);
	}

}
