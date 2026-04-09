package com.legal.service;

import org.springframework.stereotype.Service;

@Service
public class SimplifyService {

    public String simplifyText(String text) {

        String simplified = text;

        simplified = simplified.replace("hereinafter", "from now on");
        simplified = simplified.replace("pursuant to", "under");
        simplified = simplified.replace("notwithstanding", "despite");
        simplified = simplified.replace("aforementioned", "mentioned above");

        return simplified;
    }
}