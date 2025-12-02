package com.nourproject.backend.services.interfaces;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.container.ContainerDto;
import com.nourproject.backend.dtos.container.ContainerUpdateDto;
import com.nourproject.backend.entities.Container;
import com.nourproject.backend.enums.ContainerStatus;

public interface ContainerService {

    Response findAll();

    Response findById(String id);

    Response findByPickUpPointId(String pickUpPointId);

    Response findByStatus(ContainerStatus containerStatus);

    Response save(ContainerDto containerDto);

    Response updateById(String id, ContainerUpdateDto containerUpdateDto);

    Response deleteById(String id);
}
