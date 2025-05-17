package event.nbc;

import org.springframework.boot.SpringApplication;

public class TestEventApplication {

    public static void main(String[] args) {
        SpringApplication.from(EventApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
