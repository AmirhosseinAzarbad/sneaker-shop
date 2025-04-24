package ir.jiring.sneakershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//@ComponentScan(basePackages = {"config", "jwt","services", "repositories",})
//@EntityScan("ir/jiring/sneakershop/models")
@SpringBootApplication
public class SneakerShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SneakerShopApplication.class, args);
    }

}
