package pl.edu.pw.mini.ingreedio.api.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pw.mini.ingreedio.api.mapper.AuthInfoMapper;
import pl.edu.pw.mini.ingreedio.api.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.repository.AuthRepository;
import pl.edu.pw.mini.ingreedio.api.security.JwtUserClaims;

@Service
@AllArgsConstructor
public class JwtClaimsService {
    AuthRepository authRepository;
    AuthInfoMapper authInfoMapper;

    public JwtUserClaims getJwtUserClaimsByUsername(String username)
        throws UsernameNotFoundException {
        AuthInfo authInfo = authRepository.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("User '" + username + "' not found!"));

        return authInfoMapper.toTokenClaims(authInfo);
    }

    public JwtUserClaims getJwtUserClaimsByAuthInfo(AuthInfo authInfo) {
        return authInfoMapper.toTokenClaims(authInfo);
    }
}
