package com.example.qisynthbanking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String user_id;
    private String first_name;
    private String last_name;
    private String other_name;
}
