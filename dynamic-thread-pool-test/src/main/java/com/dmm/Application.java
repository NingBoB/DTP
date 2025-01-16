package com.dmm;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author Mean
 * @date 2025/1/4 15:16
 * @description Application
 */
@SpringBootApplication
@Configurable
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}


}
