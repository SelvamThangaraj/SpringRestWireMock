package com.in28minutes.microservices.currencyconversionservice;

import static org.junit.jupiter.api.Assertions.fail;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableConfigurationProperties
@SpringBootTest(classes = CurrencyConversionServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class CurrencyConversionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	CurrencyConversionController currencyConversionController;
	
	@Autowired
	Environment environment;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(options().dynamicPort());

	private ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void init() {

		// Set private field
		// ReflectionTEstUtils.setField(<classname>,<private_field_name>,<private_field_value);
		/*
		 * this.currencyConversionController.setBase("http://localhost:" +
		 * this.environment.getProperty("wiremock.server.port"));
		 */
		wireMockRule.resetMappings();
		wireMockRule.resetScenarios();
		wireMockRule.resetRequests();
	}

	@Test
	public void testOk() throws Exception{
		//registerTargetServiceApiResponse();
		CurrencyConversionBean currencyConvBean=new CurrencyConversionBean();
		currencyConvBean.setFrom("USD");
		currencyConvBean.setTo("INR");
		currencyConvBean.setQuantity(new BigDecimal(10));
		currencyConvBean.setConversionMultiple(new BigDecimal(65));
		wireMockRule.stubFor(WireMock.get(WireMock.urlEqualTo("/currency-exchange/from/USD/to/INR"))
		                              .willReturn(WireMock.aResponse().withBody(objectMapper.writeValueAsString(currencyConvBean))
									  .withStatus(HttpStatus.OK.value())));
		MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/currency-converter/from/USD/to/INR/quantity/10"))
		                          // .contentType(MediaType.APPLICATION_JSON)
								  // .param("isCustomerApporved","true")
								   //.headers(httpHeaders))
								   .andDo(print()).andReturn();
		String responseString=mvcResult.getResponse().getContentAsString();
		Assert.assertNotNull(responseString);
	}
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
