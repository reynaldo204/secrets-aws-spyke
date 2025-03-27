package com.spyke.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configurations
@ConfigurationProperties(prefix = "aws.secrets")
public class AwsSecretsConfig {
    private String arns;

    public String getArns() { return arns; }
    public void setArns(String arns) { this.arns = arns; }
}
