package atsaratbriut.school.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConfirmationEmailDto {
    String userName;
    String userLastName;
    String childFirstName;
    String childSecondName;
    String email;
    String picture;
}