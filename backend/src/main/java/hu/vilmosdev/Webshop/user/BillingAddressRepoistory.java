package hu.vilmosdev.Webshop.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingAddressRepoistory extends JpaRepository<BillingAddress, Long> {
}
