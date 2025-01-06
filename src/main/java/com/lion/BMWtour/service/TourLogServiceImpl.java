package com.lion.BMWtour.service;

import org.springframework.stereotype.Service;

import com.lion.BMWtour.entity.Region;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.entity.TourLog;
import com.lion.BMWtour.repository.TourLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TourLogServiceImpl implements TourLogService {

	private final TourLogRepository tourLogRepository;

	@Override
	public void saveTourLog(TourInfo tourInfo) {
		String address = tourInfo.getAddress();
		Region region = Region.fromAddress(address);

		TourLog tourLog = TourLog.builder()
			.tourId(tourInfo.getId())
			.tourTitle(tourInfo.getTitle())
			.tourAddress(address)
			.tourRegion(region.getName())
			.category(tourInfo.getCategory())
			.build();
		tourLogRepository.save(tourLog);
	}
}
