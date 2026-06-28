package com.personalloan.module.customer.internal.mapper;

import com.personalloan.module.customer.api.dto.CustomerProfileRequest;
import com.personalloan.module.customer.internal.entity.CustomerProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "profileStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "aadhaarNumber", ignore = true) // Set manually after encryption/uniqueness checks
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(CustomerProfileRequest request, @MappingTarget CustomerProfile entity);
}
