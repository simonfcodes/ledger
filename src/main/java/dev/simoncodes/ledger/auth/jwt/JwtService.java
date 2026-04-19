package dev.simoncodes.ledger.auth.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.simoncodes.ledger.config.JwtProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final String ISSUER_NAME = "ledger";

    private final JwtProperties jwtProps;

    private RSASSASigner signer;
    private JWSVerifier verifier;

    @PostConstruct
    private void loadKeys() {
        try {
            String privatePem = Files.readString(Path.of(jwtProps.privateKeyPath()));
            RSAKey privateRsa = (RSAKey) JWK.parseFromPEMEncodedObjects(privatePem);
            RSAPrivateKey privateKey = privateRsa.toRSAPrivateKey();
            String publicPem = Files.readString(Path.of(jwtProps.publicKeyPath()));
            RSAKey publicRsa = (RSAKey) JWK.parseFromPEMEncodedObjects(publicPem);
            RSAPublicKey publicKey = publicRsa.toRSAPublicKey();
            this.signer = new RSASSASigner(privateKey);
            this.verifier = new RSASSAVerifier(publicKey);
        } catch (Exception e) {
            throw new JwtException("Failed to load RSA keys for JWT", e);
        }
    }

    public String generateAccessToken(UUID userId) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtProps.accessTokenExpiry()))
                .issuer(ISSUER_NAME)
                .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        try {
            signedJWT.sign(this.signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new JwtException("Failed to sign JWT: " + e.getMessage(), e);
        }
    }

    public UUID validateAccessToken(String accessToken) {
        try {
            SignedJWT jwt = SignedJWT.parse(accessToken);
            boolean verified = jwt.verify(this.verifier);
            if (!verified) {
                throw new JwtException("Invalid JWT token");
            }
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            if (!claimsSet.getIssuer().equals(ISSUER_NAME)) {
                throw new JwtException("Invalid Issuer Name in JWT token");
            }
            if (claimsSet.getExpirationTime().before(new Date())) {
                throw new JwtException("Expired JWT token");
            }
            return UUID.fromString(jwt.getJWTClaimsSet().getSubject());
        } catch (JwtException e) {
            throw e;
        }
        catch (Exception e) {
            throw new JwtException("Failed to validate JWT: " + e.getMessage(), e);
        }
    }
}
