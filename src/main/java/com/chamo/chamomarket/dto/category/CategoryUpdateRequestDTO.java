package com.chamo.chamomarket.dto.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequestDTO {

    @NotEmpty
    @NotNull
    private Long id;

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private Boolean status;

}
