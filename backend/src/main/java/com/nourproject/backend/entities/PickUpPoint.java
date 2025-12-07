package com.nourproject.backend.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pickuppoints")
public class PickUpPoint {
    @MongoId(FieldType.OBJECT_ID)
    private String _id;
    
    @DocumentReference(lazy = true)  // Store container references, load on demand
    private List<Container> containers;
    private double locationLatitude;
    private double locationLongitude;
    private String address;

}
