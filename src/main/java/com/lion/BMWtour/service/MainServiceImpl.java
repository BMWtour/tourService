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
import com.lion.BMWtour.dto.main.PopularByCategoryResponse;
import com.lion.BMWtour.dto.main.PopularRegionsResponse;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.entity.TourLog;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private static final int MAIN_PAGE_ITEM_COUNT = 6;

	private final ElasticsearchTemplate elasticsearchTemplate;

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

		NativeQuery query = NativeQuery.builder()
			.withAggregation("popularRegions", Aggregation.of(a -> a.terms(
				t -> t.field("tourRegion").size(MAIN_PAGE_ITEM_COUNT))
			))
			.withMaxResults(0)
			.build();

		SearchHits<TourLog> searchHits = elasticsearchTemplate.search(query, TourLog.class);
		ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
		ElasticsearchAggregation popularRegions = aggregations.get("popularRegions");
		Aggregate aggregate = popularRegions.aggregation().getAggregate();

		List<StringTermsBucket> buckets = aggregate.sterms().buckets().array();
		return buckets.stream()
			.map(stringTermsBucket -> PopularRegionsResponse.builder()
				.region(stringTermsBucket.key().stringValue())
				.image("/img/default/region/" + stringTermsBucket.key().stringValue() + ".jpg")
				.build())
			.toList();
	}

	@Override
	public List<PopularByCategoryResponse> getPopularByCategoryList() {
		NativeQuery query = createPopularToursByCategoryQuery();

		SearchHits<TourLog> searchHits = elasticsearchTemplate.search(query, TourLog.class);
		ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
		List<StringTermsBucket> groupByCategory = aggregations.get("groupByCategory")
			.aggregation().getAggregate().sterms().buckets().array();

		return groupByCategory.stream()
			.map(stringTermsBucket -> {
				List<StringTermsBucket> groupById = stringTermsBucket.aggregations().get("groupById").sterms().buckets().array();
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
	}

	private static NativeQuery createPopularToursByCategoryQuery() {
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
		return NativeQuery.builder()
			.withAggregation("groupByCategory", Aggregation.of(a ->
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

		NativeQuery query = NativeQuery.builder()
			.withQuery(Query.findAll())
			.withSort(Sort.by(new GeoDistanceOrder("point", new GeoPoint(latitude, longitude))).ascending())
			.withMaxResults(MAIN_PAGE_ITEM_COUNT)
			.build();

		SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(query, TourInfo.class);
		return searchHits.getSearchHits().stream()
			.map(searchHit -> NearbyLocationResponse.builder()
				.id(searchHit.getId())
				.address(searchHit.getContent().getAddress())
				.title(searchHit.getContent().getTitle())
				.image("/img/default/category/" + searchHit.getContent().getCategory() + ".jpg")
				.build())
			.toList();
	}
}
