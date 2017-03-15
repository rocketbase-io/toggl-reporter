package io.rocketbase.toggl.backend.security;

import io.rocketbase.toggl.backend.repository.MongoUserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MongoUserDetailsService implements UserDetailsService {

    @Autowired
    private MongoUserDetailsRepository mongoUserDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MongoUserDetails> userDetails = mongoUserDetailsRepository.findByUsername(username.toLowerCase());
        MongoUserDetails user = userDetails.orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return user;
    }
}
