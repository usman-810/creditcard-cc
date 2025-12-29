package com.creditcard.creditcard_service.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.creditcard.creditcard_service.dto.ApiResponse;

@FeignClient(
    name = "customer-service",
    url = "${customer.service.url:http://localhost:8081}",
    fallback = CustomerServiceClientFallback.class
)
public interface CustomerServiceClient {

    @GetMapping("/api/customers/{id}")
    ApiResponse<CustomerDTO> getCustomerById(@PathVariable("id") Long id);

    @GetMapping("/api/customers/exists/{id}")
    ApiResponse<Boolean> customerExists(@PathVariable("id") Long id);
}