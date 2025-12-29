package com.creditcard.creditcard_service.feign;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.creditcard.creditcard_service.dto.ApiResponse;

@Component
@Slf4j
public class CustomerServiceClientFallback implements CustomerServiceClient {

    @Override
    public ApiResponse<CustomerDTO> getCustomerById(Long id) {
        log.error("Customer Service is down. Fallback triggered for getCustomerById: {}", id);
        return ApiResponse.error("Customer Service is temporarily unavailable");
    }

    @Override
    public ApiResponse<Boolean> customerExists(Long id) {
        log.error("Customer Service is down. Fallback triggered for customerExists: {}", id);
        return ApiResponse.error("Customer Service is temporarily unavailable");
    }
}