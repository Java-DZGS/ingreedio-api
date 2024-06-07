package pl.edu.pw.mini.ingreedio.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.mini.ingreedio.api.auth.model.AuthInfo;
import pl.edu.pw.mini.ingreedio.api.auth.security.JwtUserClaims;

@Service
@RequiredArgsConstructor
public class JwtClaimsService {
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public JwtUserClaims getJwtUserClaimsByAuthInfo(AuthInfo authInfo) {
        return modelMapper.map(authInfo, JwtUserClaims.JwtUserClaimsBuilder.class).build();
    }
}
