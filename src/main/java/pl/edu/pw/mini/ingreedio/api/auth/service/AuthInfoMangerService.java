package pl.edu.pw.mini.ingreedio.api.auth.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.exception.UserAlreadyExistsException;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.repository.AuthInfoRepository;

@Service
@RequiredArgsConstructor
public class AuthInfoMangerService {
    private final AuthInfoRepository authInfoRepository;

    @Transactional
    public AuthInfo save(AuthInfo authInfo) {
        try {
            return authInfoRepository.save(authInfo);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }
    }

    @Transactional(readOnly = true)
    public Optional<AuthInfo> getByUsername(String username) throws UsernameNotFoundException {
        return authInfoRepository.findByUsername(username);
    }
}
