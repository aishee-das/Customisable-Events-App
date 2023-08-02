import model.Consumer;
import model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserPasswordUnitTests {
    @Test
    void samePasswordMatches() {
        User testUser = new Consumer(
                "Amy McDonald",
                "test@mail.com",
                "004499",
                "Stockbridge, Edinburgh",
                "123456"
        );
        assertTrue(testUser.checkPasswordMatch("123456"));
    }

    @Test
    void differentPasswordDoesNotMatch() {
        User testUser = new Consumer(
                "Super Woman",
                "test@mail.com",
                "004499",
                "Washington DC, USA",
                "123456"
        );
        assertFalse(testUser.checkPasswordMatch("12345"));
        assertFalse(testUser.checkPasswordMatch("1234567"));
        assertFalse(testUser.checkPasswordMatch(""));
        assertFalse(testUser.checkPasswordMatch("password"));
        assertFalse(testUser.checkPasswordMatch("admin"));
        assertFalse(testUser.checkPasswordMatch("123456789"));
    }

    @Test
    void passwordIsNotLoggedInPlaintext() {
        User testUser = new Consumer(
                "Ghost Spook",
                "test@mail.com",
                "004499",
                "Shenzhen, China",
                "123456"
        );
        assertFalse(testUser.toString().contains("123456"));
    }
}
