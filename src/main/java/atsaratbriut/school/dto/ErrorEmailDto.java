package atsaratbriut.school.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Deprecated
public class ErrorEmailDto {
    String email;
    String userFirstName;
    String userLastName;
}
