package com.nourproject.backend.services.interfaces;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteUpdateDto;

import java.time.LocalDateTime;

public interface RouteService {

    Response findAll();

    Response findById(String id);

    Response findByRouteDate(LocalDateTime routeDate);

    Response findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Response checkDuplicate(java.util.List<String> pickUpPointIds);

    Response save(RouteDto routeDto);

    Response updateById(String id, RouteUpdateDto routeUpdateDto);

    Response deleteById(String id);
}
