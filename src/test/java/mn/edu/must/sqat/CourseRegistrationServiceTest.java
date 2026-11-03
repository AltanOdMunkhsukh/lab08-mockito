package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;
    CourseRegistrationService service;

    @BeforeEach
    void setUp() { service = new CourseRegistrationService(repository, emailSender); }

    @Test
    void register_activeStudent_returnsOkSavesAndSendsEmail() {
        Student student = new Student("S1", "s1@must.edu.mn", true, List.of("CS101"));
        when(repository.findStudent("S1")).thenReturn(student);
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101"));

        String result = service.register("S1", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S1", "CS201");
        verify(emailSender).send(eq("s1@must.edu.mn"), anyString(), anyString());
    }


    @Test
    void register_unknownStudent_returnsErrorNoStudentAndDoesNothingElse() {
        when(repository.findStudent("S99")).thenReturn(null);

        String result = service.register("S99", "CS201");

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void register_inactiveStudent_returnsErrorInactiveAndDoesNotTouchEmail() {
        Student inactive = new Student("S2", "s2@must.edu.mn", false, List.of());
        when(repository.findStudent("S2")).thenReturn(inactive);

        String result = service.register("S2", "CS201");

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verifyNoInteractions(emailSender);
    }

    @Test
    void register_missingPrerequisites_returnsErrorPrerequisitesAndDoesNotSave() {
        Student active = new Student("S4", "s4@must.edu.mn", true, List.of("CS101"));
        when(repository.findStudent("S4")).thenReturn(active);
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101, CS102"));

        String result = service.register("S4", "CS201");

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }
}