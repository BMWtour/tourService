package com.lion.BMWtour.entity;

import java.util.Arrays;

public enum Region {

	GANGWON("강원"),
	GYEONGGI("경기"),
	GYEONGSANGNAMDO("경상남도"),
	GYEONGSANGBUKDO("경상북도"),
	GWANGJU("광주"),
	DAEGU("대구"),
	DAEJEON("대전"),
	BUSAN("부산"),
	SEOUL("서울"),
	INCHEON("인천"),
	JEOLLANAMDO("전라남도"),
	JEONBUK("전북"),
	JEJU("제주"),
	CHUNGCHEONGNAMDO("충청남도"),
	CHUNGCHEONGBUKDO("충청북도");

	private final String name;

	Region(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Region fromAddress(String address) {
		return Arrays.stream(Region.values())
			.filter(region -> address.contains(region.getName()))
			.findFirst()
			.orElse(null);
	}
}
