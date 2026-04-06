package com.SubString.products.products_app.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class RoleDto {



    private UUID id = UUID.randomUUID();

    private String name;
}
