package com.OAuth2.repository;

import com.OAuth2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// @Repository 어노테이션이 없어도 IoC가 된다. JpaRepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User,Integer> {
    public User findByUsername(String username);

    public Optional<User> findByProviderAndProviderId(String provider, String providerId);
}