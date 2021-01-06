package atsaratbriut.school.service.email;

import atsaratbriut.school.dto.ConfirmationEmailDto;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    String sendSchoolEmail(ConfirmationEmailDto confirmationEmailDto);
    void emailError( String lastName, String firstName);
}
