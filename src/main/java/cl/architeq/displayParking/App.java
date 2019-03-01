package cl.architeq.displayParking;


import cl.architeq.displayParking.service.GpioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class App implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}


	@Autowired
	GpioService gpioService;

	@Override
	public void run(String... args) throws Exception {

		System.out.println("Servicio Display LCD - Control Estacionamiento .. ");

		this.gpioService.executeAsync();


	}
}
