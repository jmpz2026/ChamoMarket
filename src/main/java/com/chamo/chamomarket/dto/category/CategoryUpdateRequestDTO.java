package com.chamo.chamomarket.dto.category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequestDTO {

    @NotNull
    @Min(1)
    private Long id;

    @NotEmpty
    @NotNull
    private String name;

    @NotNull
    private Boolean status;

}
