package com.runcombi.server.domain.run.entity;

import com.runcombi.server.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunPet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long runPetId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id")
    private Run run;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private Integer petCal; // 펫 소모 칼로리

}
