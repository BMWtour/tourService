package com.lion.BMWtour.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "tourlogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourLog {

	@Id
	private String id;
	@Field(type = FieldType.Keyword)
	private String tourId;
	@Field(type = FieldType.Keyword)
	private String tourTitle;
	@Field(type = FieldType.Keyword)
	private String tourAddress;
	@Field(type = FieldType.Keyword)
	private String tourRegion;
	@Field(type = FieldType.Keyword)
	private String category;
}
