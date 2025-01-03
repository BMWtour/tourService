package com.lion.BMWtour.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lion.BMWtour.dto.main.NearbyLocationResponse;
import com.lion.BMWtour.dto.main.PopularByCategoryResponse;
import com.lion.BMWtour.dto.main.PopularRegionsResponse;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.entity.TourLog;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.GeoDistanceSort;
import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private static final int MAIN_PAGE_ITEM_COUNT = 6;

	private final ElasticsearchClient elasticsearchClient;

	@Override
	public List<PopularRegionsResponse> getPopularRegionsList() {
		//  """
		// 	{
		// 	  "size": 0,
		// 	  "aggs": {
		// 	    "popularRegions": {
		// 	      "terms": {
		// 	        "field": "tourRegion",
		// 	        "size": 6
		// 	      }
		// 	    }
		// 	  }
		// 	}
		// 	"""

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
	public List<PopularByCategoryResponse> getPopularByCategoryList() {
		SearchRequest searchRequest = createPopularToursByCategorySearchRequest();

		try {
			SearchResponse<TourLog> search = elasticsearchClient.search(searchRequest, TourLog.class);

			List<StringTermsBucket> groupByCategory = search.aggregations().get("groupByCategory").sterms().buckets().array();
			return groupByCategory.stream()
				.map(categoryBucket -> {
					List<StringTermsBucket> groupById = categoryBucket.aggregations().get("groupById").sterms().buckets().array();
					HitsMetadata<JsonData> topTour = groupById.get(0).aggregations().get("topTour").topHits().hits();
					return topTour.hits().get(0).source().to(TourLog.class);
				})
				.map(tourLog -> PopularByCategoryResponse.builder()
					.category(tourLog.getCategory())
					.id(tourLog.getTourId())
					.title(tourLog.getTourTitle())
					.address(tourLog.getTourAddress())
					.image("/img/default/category/" + tourLog.getCategory() + ".jpg")
					.build())
				.toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static SearchRequest createPopularToursByCategorySearchRequest() {
		// {
		// 	"size": 0,
		// 	"aggs": {
		// 	"groupByCategory": {
		// 		"terms": {
		// 			"field": "category",
		// 			"size": 6
		// 		},
		// 		"aggs": {
		// 			"groupById": {
		// 				"terms": {
		// 					"field": "tourId",
		// 					"size": 1
		// 				},
		// 				"aggs": {
		// 					"topTour": {
		// 						"top_hits": {
		// 							"size": 1
		// 						}
		// 					}
		// 				}
		// 			}
		// 		}
		// 	 }
		//  }
		// }
		return new SearchRequest.Builder()
			.index("tourlogs")
			.size(0)
			.aggregations("groupByCategory", Aggregation.of(a ->
				a.terms(
						t -> t.field("category")
							.size(MAIN_PAGE_ITEM_COUNT)
					)
					.aggregations("groupById", Aggregation.of(subA ->
						subA.terms(
								subT -> subT.field("tourId")
									.size(1)
							)
							.aggregations("topTour", Aggregation.of(topA ->
								topA.topHits(th -> th.size(1)))
							)
					))
			))
			.build();
	}

	@Override
	public List<NearbyLocationResponse> getNearbyLocationList(Double latitude, Double longitude) {
		//  """
		// 	{
		// 	  "size": 6,
		// 	  "query": {
		// 	    "match_all": {}
		// 	  },
		// 	  "sort": [
		// 	    {
		// 	      "_geo_distance": {
		// 	        "point": {
		// 	          "lat": 37.7749,
		// 	          "lon": -122.4194
		// 	        },
		// 	        "order": "asc",
		// 	        "unit": "m"
		// 	      }
		// 	    }
		// 	  ]
		// 	}
		// 	"""

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
				.map(hit -> {
					TourInfo tourInfo = hit.source();
					tourInfo.setId(hit.id());
					return tourInfo;
				})
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
