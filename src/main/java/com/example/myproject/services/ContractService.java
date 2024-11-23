package com.example.myproject.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ContractService {

    @Cacheable(value = "contractCache", key = "#id")
    public String getContractId(Long id) {
        return id + "ABC";
    }
}
