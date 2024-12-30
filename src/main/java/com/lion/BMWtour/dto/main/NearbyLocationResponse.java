package com.lion.BMWtour.dto.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class NearbyLocationResponse {

	private String id;
	private String title;
	private String image;
	private String address;
}
