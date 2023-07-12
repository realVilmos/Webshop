package hu.mylildev;
import hu.mylildev.model.User;
import hu.mylildev.repo.UserRepo;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUser(){
        User user = new User();
        user.setEmail("a@valahol.lol");
        user.setUsername("asd");
        user.setPassword("dsadsad");
        user.setFirstName("Mohamed");
        user.setLastName("Nem-Magyar");

        User savedUser = userRepo.save(user);

        User existUser = entityManager.find(User.class, savedUser.getId());

        assertThat(existUser.getEmail(), is(equalTo(user.getEmail())));
    }

    @Test
    public void testFindUserByEmail(){
        String email = "a@asdasdad.lol";
        User user = userRepo.findByEmail(email);

        assertThat(user, is(notNullValue()));
    }

}
