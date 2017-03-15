package io.rocketbase.toggl.backend.security;

import io.rocketbase.toggl.backend.repository.MongoUserDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MongoUserService {

    @Resource
    private MongoUserDetailsRepository repository;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public MongoUserDetails register(MongoUserDetails userDetails) {
        String username = userDetails.getUsername()
                .toLowerCase();
        if (repository.findByUsername(username)
                .isPresent()) {
            throw new RuntimeException("username already in use");
        }
        userDetails.setUsername(username);
        userDetails.setPassword(encoder.encode(userDetails.getPassword()));
        return repository.save(userDetails);
    }

    public void checkInitialState() {
        if (repository.count() == 0) {
            repository.save(MongoUserDetails.builder()
                    .enabled(true)
                    .password(encoder.encode("admin"))
                    .role(UserRole.ROLE_ADMIN)
                    .username("admin")
                    .build());
            log.info("initialized admin user");
        }
    }

    public List<MongoUserDetails> findAll() {
        return repository.findAll();
    }

    public MongoUserDetails updateDetailsExceptPassword(MongoUserDetails entity) {
        MongoUserDetails dbEntity = repository.findOne(entity.getId());
        BeanUtils.copyProperties(entity, dbEntity, "password");
        return repository.save(dbEntity);
    }

    public void delete(MongoUserDetails entity) {
        Object p = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (p instanceof MongoUserDetails && ((MongoUserDetails) p).getUsername()
                .equals(entity.getUsername())) {
            throw new RuntimeException("u cannot delete yourself!");
        }
        repository.delete(entity);
    }

    public MongoUserDetails updatePassword(MongoUserDetails user, String newPassword) {
        MongoUserDetails dbEntity = repository.findOne(user.getId());
        dbEntity.setPassword(encoder.encode(newPassword));
        return repository.save(dbEntity);
    }
}