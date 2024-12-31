package com.lion.BMWtour.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import com.lion.BMWtour.dto.main.NearbyLocationResponse;
import com.lion.BMWtour.entity.TourInfo;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private static final int MAIN_PAGE_ITEM_COUNT = 6;
	private final ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public List<NearbyLocationResponse> getNearbyLocationList(Double latitude, Double longitude) {
		NativeQuery query = NativeQuery.builder()
			.withQuery(Query.findAll())
			.withSort(Sort.by(new GeoDistanceOrder("point", new GeoPoint(latitude, longitude))).ascending())
			.withMaxResults(MAIN_PAGE_ITEM_COUNT)
			.build();

		SearchHits<TourInfo> search = elasticsearchTemplate.search(query, TourInfo.class);
		return search.getSearchHits().stream()
			.map(searchHit -> NearbyLocationResponse.builder()
				.id(searchHit.getId())
				.address(searchHit.getContent().getAddress())
				.title(searchHit.getContent().getTitle())
				.image("/img/default/category/" + searchHit.getContent().getCategory() + ".jpg")
				.build())
			.toList();
	}
}
