package com.nourproject.backend.entities;
import com.nourproject.backend.enums.VehiculeType;
import com.nourproject.backend.enums.VehiculeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "vehicules")
public class Vehicule {
    @Id
    private String _id;
    private String matricul;
    private double capacity;
    private VehiculeStatus vehiculeStatus;
    private VehiculeType vehiculeType;
    @Builder.Default
    List<User> users=new ArrayList<User>();


}
