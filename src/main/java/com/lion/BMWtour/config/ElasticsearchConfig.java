package com.lion.BMWtour.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

	@Value("${spring.elasticsearch.host}")
	private String host;
	@Value("${spring.elasticsearch.port}")
	private int port;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public ElasticsearchClient elasticsearchClient() {
		RestClient restClient = RestClient.builder(new HttpHost(host, port)).build();

		// Spring Boot의 ObjectMapper 사용
		JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);
		RestClientTransport transport = new RestClientTransport(restClient, jsonpMapper);

		return new ElasticsearchClient(transport);
	}
}
