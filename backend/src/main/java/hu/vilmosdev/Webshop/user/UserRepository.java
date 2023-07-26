package hu.vilmosdev.Webshop.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
  public User findByVerificationCode(String code);

}
