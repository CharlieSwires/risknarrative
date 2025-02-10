package com.charlie.truproxy;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.tomakehurst.wiremock.WireMockServer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TruProxyApplication.class)
class CompanyIntegrationTest {
	private static final String BASE_URL = "http://localhost:8080";
	@Autowired
	private WebTestClient webTestClient;
	@Autowired
	private WebClient webClient;

	@Autowired
	private OfficerRepository officerRepository;
	@Autowired
	private CompanyRepository companyRepository;

	@Value("${api.key}")
	private String apiKey;

	private WireMockServer wireMockServer;

	@BeforeEach
	void setupWireMock() {
		wireMockServer = new WireMockServer(8089);
		wireMockServer.start();
		configureFor("localhost", 8089);

		// ✅ Mock API response
		stubFor(post(urlPathEqualTo("/api/company/search"))
				.withHeader("x-api-key", equalTo(apiKey))
				.withRequestBody(equalToJson("{\"companyName\": \"BBC LIMITED\"}"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{
								    "total_results": 1,
								    "items": [
								        {
								            "company_number": "06500244",
								            "company_type": "ltd",
								            "title": "BBC LIMITED",
								            "company_status": "active",
								            "date_of_creation": "2008-02-11",
								            "address": {
								                "locality": "Retford",
								                "postal_code": "DN22 0AD",
								                "premises": "Boswell Cottage Main Street",
								                "address_line_1": "North Leverton",
								                "country": "England"
								            },
								            "officers": [
								                {
								                    "name": "BOXALL, Sarah Victoria",
								                    "officer_role": "secretary",
								                    "appointed_on": "2008-02-11",
								                    "address": {
								                        "premises": "5",
								                        "locality": "London",
								                        "address_line_1": "Cranford Close",
								                        "country": "England",
								                        "postal_code": "SW20 0DP"
								                    }
								                }
								            ]
								        }
								    ]
								}
								""")));
		officerRepository.deleteAll();
		companyRepository.deleteAll();

	}

	@AfterEach
	void stopWireMock() {
		wireMockServer.stop(); // ✅ Stop WireMock after test
	}

	@Test
	void testSearchCompany() {
		CompanyResponse response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/put")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{"
						+ "    	            \"company_number\": \"06500244\","
						+ "    	            \"company_type\": \"ltd\","
						+ "    	            \"title\": \"BBC LIMITED\","
						+ "    	            \"company_status\": \"active\","
						+ "    	            \"date_of_creation\": \"2008-02-11\","
						+ "    	            \"address\": {"
						+ "    	                \"locality\": \"Retford\","
						+ "    	                \"postal_code\": \"DN22 0AD\","
						+ "    	                \"premises\": \"Boswell Cottage Main Street\","
						+ "    	                \"address_line_1\": \"North Leverton\","
						+ "    	                \"country\": \"England\""
						+ "    	            },"
						+ "    	            \"officers\": ["
						+ "    	                {"
						+ "    	                    \"name\": \"BOXALL, Sarah Victoria\","
						+ "    	                    \"officer_role\": \"secretary\","
						+ "    	                    \"appointed_on\": \"2008-02-11\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                }"
						+ "    	            ]"
						+ "    	        }")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();

		//System.out.println("------"+response.toString());
		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "true")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		//System.out.println("+++++++"+response.toString());

		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(1);

		// 🔹 Check first company's details
		assertThat(response.getItems()).isNotEmpty();
		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
	}
	@Test
	void testSearchCompanyByNumber() {
		CompanyResponse response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/put")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{"
						+ "    	            \"company_number\": \"06500244\","
						+ "    	            \"company_type\": \"ltd\","
						+ "    	            \"title\": \"BBC LIMITED\","
						+ "    	            \"company_status\": \"active\","
						+ "    	            \"date_of_creation\": \"2008-02-11\","
						+ "    	            \"address\": {"
						+ "    	                \"locality\": \"Retford\","
						+ "    	                \"postal_code\": \"DN22 0AD\","
						+ "    	                \"premises\": \"Boswell Cottage Main Street\","
						+ "    	                \"address_line_1\": \"North Leverton\","
						+ "    	                \"country\": \"England\""
						+ "    	            },"
						+ "    	            \"officers\": ["
						+ "    	                {"
						+ "    	                    \"name\": \"BOXALL, Sarah Victoria\","
						+ "    	                    \"officer_role\": \"secretary\","
						+ "    	                    \"appointed_on\": \"2008-02-11\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                }"
						+ "    	            ]"
						+ "    	        }")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();

		//System.out.println("------"+response.toString());
		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "true")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyNumber\": \"06500244\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		//System.out.println("+++++++"+response.toString());

		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(1);

		// 🔹 Check first company's details
		assertThat(response.getItems()).isNotEmpty();
		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
	}	

	@Test
	void testSearchCompanyByConflict() {
		CompanyResponse response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/put")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{"
						+ "    	            \"company_number\": \"06500244\","
						+ "    	            \"company_type\": \"ltd\","
						+ "    	            \"title\": \"BBC LIMITED\","
						+ "    	            \"company_status\": \"active\","
						+ "    	            \"date_of_creation\": \"2008-02-11\","
						+ "    	            \"address\": {"
						+ "    	                \"locality\": \"Retford\","
						+ "    	                \"postal_code\": \"DN22 0AD\","
						+ "    	                \"premises\": \"Boswell Cottage Main Street\","
						+ "    	                \"address_line_1\": \"North Leverton\","
						+ "    	                \"country\": \"England\""
						+ "    	            },"
						+ "    	            \"officers\": ["
						+ "    	                {"
						+ "    	                    \"name\": \"BOXALL, Sarah Victoria\","
						+ "    	                    \"officer_role\": \"secretary\","
						+ "    	                    \"appointed_on\": \"2008-02-11\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                }"
						+ "    	            ]"
						+ "    	        }")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();

		//System.out.println("------"+response.toString());
		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "true")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyName\": \"BBC LIMITED\", \"companyNumber\": \"060244\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		//System.out.println("+++++++"+response.toString());

		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(0);

	}
	@Test
	void testSearchCompanyMultipleOfficers() {
		CompanyResponse response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/put")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{"
						+ "    	            \"company_number\": \"06500244\","
						+ "    	            \"company_type\": \"ltd\","
						+ "    	            \"title\": \"BBC LIMITED\","
						+ "    	            \"company_status\": \"active\","
						+ "    	            \"date_of_creation\": \"2008-02-11\","
						+ "    	            \"address\": {"
						+ "    	                \"locality\": \"Retford\","
						+ "    	                \"postal_code\": \"DN22 0AD\","
						+ "    	                \"premises\": \"Boswell Cottage Main Street\","
						+ "    	                \"address_line_1\": \"North Leverton\","
						+ "    	                \"country\": \"England\""
						+ "    	            },"
						+ "    	            \"officers\": ["
						+ "    	                {"
						+ "    	                    \"name\": \"BOXALL, Sarah Victoria\","
						+ "    	                    \"officer_role\": \"secretary\","
						+ "    	                    \"appointed_on\": \"2008-02-11\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                },"
						+                      "{"
						+ "    	                    \"name\": \"SWIRES, Charlie\","
						+ "    	                    \"officer_role\": \"Manager\","
						+ "    	                    \"appointed_on\": \"2008-02-06\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                }"
						+ "    	            ]"
						+ "    	        }")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();

		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "true")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		//System.out.println("********"+response.toString());

		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(1);
		assertThat(response.getItems().get(0).getOfficers().size()).isEqualTo(2);

	}
	@Test
	void testSearchCompanyInactive() {
		CompanyResponse response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/put")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{"
						+ "    	            \"company_number\": \"06500244\","
						+ "    	            \"company_type\": \"ltd\","
						+ "    	            \"title\": \"BBC LIMITED\","
						+ "    	            \"company_status\": \"inactive\","
						+ "    	            \"date_of_creation\": \"2008-02-11\","
						+ "    	            \"address\": {"
						+ "    	                \"locality\": \"Retford\","
						+ "    	                \"postal_code\": \"DN22 0AD\","
						+ "    	                \"premises\": \"Boswell Cottage Main Street\","
						+ "    	                \"address_line_1\": \"North Leverton\","
						+ "    	                \"country\": \"England\""
						+ "    	            },"
						+ "    	            \"officers\": ["
						+ "    	                {"
						+ "    	                    \"name\": \"BOXALL, Sarah Victoria\","
						+ "    	                    \"officer_role\": \"secretary\","
						+ "    	                    \"appointed_on\": \"2008-02-11\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                }"
						+ "    	            ]"
						+ "    	        }")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();

		//System.out.println("------"+response.toString());
		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "true")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyNumber\": \"06500244\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		//System.out.println("+++++++"+response.toString());

		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(0);

		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "false")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		// 🔹 Check first company's details
		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(1);
		assertThat(response.getItems()).isNotEmpty();
		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
	}	

	@Test
	void testSearchCompanyResigned() {
		CompanyResponse response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/put")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{"
						+ "    	            \"company_number\": \"06500244\","
						+ "    	            \"company_type\": \"ltd\","
						+ "    	            \"title\": \"BBC LIMITED\","
						+ "    	            \"company_status\": \"active\","
						+ "    	            \"date_of_creation\": \"2008-02-11\","
						+ "    	            \"address\": {"
						+ "    	                \"locality\": \"Retford\","
						+ "    	                \"postal_code\": \"DN22 0AD\","
						+ "    	                \"premises\": \"Boswell Cottage Main Street\","
						+ "    	                \"address_line_1\": \"North Leverton\","
						+ "    	                \"country\": \"England\""
						+ "    	            },"
						+ "    	            \"officers\": ["
						+ "    	                {"
						+ "    	                    \"name\": \"BOXALL, Sarah Victoria\","
						+ "    	                    \"officer_role\": \"secretary\","
						+ "    	                    \"appointed_on\": \"2008-02-11\","
						+ "    	                    \"resigned_on\": \"2008-02-12\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                }"
						+ "    	            ]"
						+ "    	        }")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();

		//System.out.println("------"+response.toString());
		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "true")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		//System.out.println("+++++++"+response.toString());

		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(1);


		assertThat(response.getItems().get(0).getOfficers().isEmpty());
	}
	@Test
	void testProperSearchCompany() {
		CompanyResponse response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/put")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{"
						+ "    	            \"company_number\": \"06500244\","
						+ "    	            \"company_type\": \"ltd\","
						+ "    	            \"title\": \"BBC LIMITED\","
						+ "    	            \"company_status\": \"active\","
						+ "    	            \"date_of_creation\": \"2008-02-11\","
						+ "    	            \"address\": {"
						+ "    	                \"locality\": \"Retford\","
						+ "    	                \"postal_code\": \"DN22 0AD\","
						+ "    	                \"premises\": \"Boswell Cottage Main Street\","
						+ "    	                \"address_line_1\": \"North Leverton\","
						+ "    	                \"country\": \"England\""
						+ "    	            },"
						+ "    	            \"officers\": ["
						+ "    	                {"
						+ "    	                    \"name\": \"BOXALL, Sarah Victoria\","
						+ "    	                    \"officer_role\": \"secretary\","
						+ "    	                    \"appointed_on\": \"2008-02-11\","
						+ "    	                    \"address\": {"
						+ "    	                        \"premises\": \"5\","
						+ "    	                        \"locality\": \"London\","
						+ "    	                        \"address_line_1\": \"Cranford Close\","
						+ "    	                        \"country\": \"England\","
						+ "    	                        \"postal_code\": \"SW20 0DP\""
						+ "    	                    }"
						+ "    	                }"
						+ "    	            ]"
						+ "    	        }")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		
		response = webTestClient.post()
				.uri(uriBuilder -> uriBuilder.path("/api/company/search")
						.queryParam("onlyActive", "true")
						.build())
				.header("x-api-key", apiKey)
				.header("Content-Type", "application/json")  
				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody(CompanyResponse.class)
				.returnResult()
				.getResponseBody();
		//System.out.println("+++++++"+response.toString());

		// 🔹 Ensure response is not null
		assertThat(response).isNotNull();

		// 🔹 Check total results count
		assertThat(response.getTotalResults()).isEqualTo(1);

		// 🔹 Check first company's details
		assertThat(response.getItems()).isNotEmpty();
		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
	}
//	@Test
//	void testSearchCompanyProper() {
//		//System.out.println("------"+response.toString());
//		CompanyResponse response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path(BASE_URL+"/api/company/search-proper")
//						.queryParam("onlyActive", "true")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		//System.out.println("+++++++"+response.toString());
//
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(1);
//
//		// 🔹 Check first company's details
//		assertThat(response.getItems()).isNotEmpty();
//		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
//		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
//	}
//	@Test
//	void testSearchCompanyByNumberProper() {
//		CompanyResponse response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path(BASE_URL+"/api/company/search-proper")
//						.queryParam("onlyActive", "true")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyNumber\": \"06500244\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		//System.out.println("+++++++"+response.toString());
//
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(1);
//
//		// 🔹 Check first company's details
//		assertThat(response.getItems()).isNotEmpty();
//		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
//		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
//	}	
//
//	@Test
//	void testSearchCompanyByConflictProper() {
//		CompanyResponse response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path(BASE_URL+"/api/company/search-proper")
//						.queryParam("onlyActive", "true")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyName\": \"BBC LIMITED\", \"companyNumber\": \"060244\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		//System.out.println("+++++++"+response.toString());
//
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(0);
//
//	}
//	@Test
//	void testSearchCompanyMultipleOfficersProper() {
//		CompanyResponse response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path(BASE_URL+"/api/company/search-proper")
//						.queryParam("onlyActive", "true")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		//System.out.println("********"+response.toString());
//
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(1);
//		assertThat(response.getItems().get(0).getOfficers().size()).isEqualTo(2);
//
//	}
//	@Test
//	void testSearchCompanyInactiveProper() {
//		CompanyResponse response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path(BASE_URL+"/api/company/search-proper")
//						.queryParam("onlyActive", "true")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyNumber\": \"06500244\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		//System.out.println("+++++++"+response.toString());
//
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(0);
//
//		response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path("/api/company/search-proper")
//						.queryParam("onlyActive", "false")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		// 🔹 Check first company's details
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(1);
//		assertThat(response.getItems()).isNotEmpty();
//		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
//		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
//	}	
//
//	@Test
//	void testSearchCompanyResignedProper() {
//		CompanyResponse response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path(BASE_URL+"/api/company/search-proper")
//						.queryParam("onlyActive", "true")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		//System.out.println("+++++++"+response.toString());
//
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(1);
//
//
//		assertThat(response.getItems().get(0).getOfficers().isEmpty());
//	}
//	@Test
//	void testProperSearchCompanyProper() {
//		CompanyResponse response = webClient.post()
//				.uri(uriBuilder -> uriBuilder.path(BASE_URL+"/api/company/search-proper")
//						.queryParam("onlyActive", "true")
//						.build())
//				.header("x-api-key", apiKey)
//				.header("Content-Type", "application/json")  
//				.bodyValue("{\"companyName\": \"BBC LIMITED\"}")
//				.retrieve()
//	            .bodyToMono(CompanyResponse.class) 
//	            .block();
//		//System.out.println("+++++++"+response.toString());
//
//		// 🔹 Ensure response is not null
//		assertThat(response).isNotNull();
//
//		// 🔹 Check total results count
//		assertThat(response.getTotalResults()).isEqualTo(1);
//
//		// 🔹 Check first company's details
//		assertThat(response.getItems()).isNotEmpty();
//		assertThat(response.getItems().get(0).getCompanyNumber()).isEqualTo("06500244");
//		assertThat(response.getItems().get(0).getTitle()).isEqualTo("BBC LIMITED");
//	}
}

@DataJpaTest
class CompanyRepositoryTest {

	@Autowired
	private CompanyRepository companyRepository;

	@Test
	void testSaveAndFindCompany() {
		CompanyEntity company = new CompanyEntity(
				"06500244", "ltd", "BBC LIMITED", "active", "2008-02-11", null, List.of()
				);
		companyRepository.save(company);

		Optional<CompanyEntity> foundCompany = companyRepository.findByCompanyNumber("06500244");
		assertThat(foundCompany).isPresent();
		assertThat(foundCompany.get().getTitle()).isEqualTo("BBC LIMITED");
	}
}

