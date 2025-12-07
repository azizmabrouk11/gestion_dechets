package com.nourproject.backend.entities;

import com.nourproject.backend.enums.ContainerStatus;
import com.nourproject.backend.enums.ContainerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "containers")
public class Container {
    @Id
    private String _id;
    private ContainerType containerType;
    private double capacity;
    private double fillLevel;
    private ContainerStatus containerStatus;
    private String pickUpPointId;

}
