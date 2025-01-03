package com.lion.BMWtour.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.lion.BMWtour.dto.main.NearbyLocationResponse;
import com.lion.BMWtour.dto.main.PopularRegionsResponse;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.entity.TourLog;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.GeoDistanceSort;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private static final int MAIN_PAGE_ITEM_COUNT = 6;

	private final ElasticsearchClient elasticsearchClient;

	@Override
	public List<PopularRegionsResponse> getPopularRegionsList() {
		SearchRequest searchRequest = new SearchRequest.Builder()
			.index("tourlogs")
			.size(0)
			.aggregations("popularRegions", Aggregation.of(a ->
				a.terms(t -> t.field("tourRegion")
					.size(MAIN_PAGE_ITEM_COUNT))))
			.build();

		try {
			SearchResponse<TourLog> searchResponse = elasticsearchClient.search(searchRequest, TourLog.class);
			return searchResponse.aggregations().get("popularRegions")
				.sterms().buckets().array().stream()
				.map(bucket -> PopularRegionsResponse.builder()
					.region(bucket.key().stringValue())
					.image("/img/default/region/" + bucket.key().stringValue() + ".jpg")
					.build())
				.toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<NearbyLocationResponse> getNearbyLocationList(Double latitude, Double longitude) {
		SearchRequest searchRequest = new SearchRequest.Builder()
			.index("tourinfos")
			.size(MAIN_PAGE_ITEM_COUNT)
			.query(q -> q.matchAll(m -> m))
			.sort(s ->
				s.geoDistance(GeoDistanceSort.of(g ->
					g.field("point")
						.location(l -> l.latlon(LatLonGeoLocation.of(ll -> ll.lat(latitude).lon(longitude))))
						.order(SortOrder.Asc)
				))
			)
			.build();

		try {
			SearchResponse<TourInfo> searchResponse = elasticsearchClient.search(searchRequest, TourInfo.class);
			return searchResponse.hits().hits().stream()
				.map(Hit::source)
				.map(tourInfo -> NearbyLocationResponse.builder()
					.id(tourInfo.getId())
					.address(tourInfo.getAddress())
					.title(tourInfo.getTitle())
					.image("/img/default/category/" + tourInfo.getCategory() + ".jpg")
					.build())
				.toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
