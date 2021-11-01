package com.example.workflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "Delegate")
public class DelegateDto {
    @ApiModelProperty(name = "name", dataType = "java.lang.String", value = "Имя делегата", required = false, position = 1)
    @JsonProperty("name")
    private String name;
}
