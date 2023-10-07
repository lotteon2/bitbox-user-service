package com.bixbox.user;

import com.bixbox.user.domain.member.api.MemberControllerTest;
import com.bixbox.user.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
		controllers = {
				MemberControllerTest.class
		}
)
class ControllerTestSupport {

	@Autowired protected MockMvc mockMvc;
	@Autowired protected ObjectMapper objectMapper;
	@Autowired protected MemberService memberService;

}
