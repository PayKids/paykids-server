package depth.main_project.PayKids_Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PayKidsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayKidsServerApplication.class, args);
	}

}
