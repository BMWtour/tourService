package com.lion.BMWtour.dto.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PopularRegionsResponse {

	private String region;
	private String image;
}
