package com.dlim2012.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "custom.security.rsa")
public record RsaKeys(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey
) {
}
