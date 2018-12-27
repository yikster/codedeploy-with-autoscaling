package com.amazonaws.codedeploy.demo;

import com.amazonaws.codedeploy.demo.controller.IndexController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	private IndexController indexController;

	@Test
	public void contextLoads() {
		assertThat(indexController).isNotNull();

	}


}

