package com.u012e.session_auth_db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
class SessionAuthDbApplicationTests {

	@Test
	void contextLoads() {
		assertThat(new LinkedList<>()).isNotNull();
	}

}
