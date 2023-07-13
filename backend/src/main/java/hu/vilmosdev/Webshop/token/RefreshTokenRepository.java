package hu.vilmosdev.Webshop.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  @Query(value = """
    select t from RefreshToken t inner join User u on t.user.id = u.id where u.id = :id and (t.expired = false or t.revoked = false)
    """)
  List<RefreshToken> findAllValidTokenByUser(Long id);
  Optional<RefreshToken> findByToken(String token);


}
