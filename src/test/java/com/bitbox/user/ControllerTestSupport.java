package com.bitbox.user;

import com.bitbox.user.service.MemberService;
import com.bitbox.user.domain.member.api.MemberControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
