package com.dlim2012.clients.security.dto;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "custom.security.rsa")
public record PublicKey(
        RSAPublicKey publicKey
) {
}
