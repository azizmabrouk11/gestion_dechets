package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.route.RouteDto;
import com.nourproject.backend.dtos.route.RouteUpdateDto;
import com.nourproject.backend.entities.Route;
import com.nourproject.backend.exceptions.NotFoundException;
import com.nourproject.backend.mappers.RouteMapper;
import com.nourproject.backend.repositories.RouteRepository;
import com.nourproject.backend.services.interfaces.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteMapper routeMapper;
    private final RouteRepository routeRepository;

    @Override
    public Response findAll() {
        List<RouteDto> list = routeRepository.findAll().stream()
                .map(routeMapper::routeToRouteDto).toList();

        return Response.builder()
                .status(200)
                .message("List of routes retrieved successfully")
                .routes(list)
                .build();
    }

    @Override
    public Response findById(String id) {
        RouteDto routeDto = routeRepository.findById(id)
                .map(routeMapper::routeToRouteDto)
                .orElseThrow(() -> new NotFoundException("Route with ID " + id + " not found"));
        
        return Response.builder()
                .status(200)
                .message("Route retrieved successfully")
                .route(routeDto)
                .build();
    }

    @Override
    public Response findByRouteDate(LocalDateTime routeDate) {
        List<RouteDto> list = routeRepository.findByRouteDate(routeDate).stream()
                .map(routeMapper::routeToRouteDto).toList();
        
        return Response.builder()
                .status(200)
                .message("Routes for date retrieved successfully")
                .routes(list)
                .build();
    }

    @Override
    public Response findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<RouteDto> list = routeRepository.findByRouteDateBetween(startDate, endDate).stream()
                .map(routeMapper::routeToRouteDto).toList();
        
        return Response.builder()
                .status(200)
                .message("Routes in date range retrieved successfully")
                .routes(list)
                .build();
    }

    @Override
    public Response save(RouteDto routeDto) {
        Route route = routeMapper.routeDtoToRoute(routeDto);
        Route savedRoute = routeRepository.save(route);
        RouteDto savedDto = routeMapper.routeToRouteDto(savedRoute);
        
        return Response.builder()
                .status(201)
                .message("Route created successfully")
                .route(savedDto)
                .build();
    }

    @Override
    public Response updateById(String id, RouteUpdateDto routeUpdateDto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route with ID " + id + " not found"));
        
        routeMapper.updateRouteFromDto(routeUpdateDto, route);
        Route updatedRoute = routeRepository.save(route);
        RouteDto updatedDto = routeMapper.routeToRouteDto(updatedRoute);
        
        return Response.builder()
                .status(200)
                .message("Route updated successfully")
                .route(updatedDto)
                .build();
    }

    @Override
    @Transactional
    public Response deleteById(String id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route with ID " + id + " not found"));
        
        RouteDto deletedDto = routeMapper.routeToRouteDto(route);
        routeRepository.delete(route);
        
        return Response.builder()
                .status(200)
                .message("Route deleted successfully")
                .route(deletedDto)
                .build();
    }
}
