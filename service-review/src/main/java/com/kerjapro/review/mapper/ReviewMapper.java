package com.kerjapro.review.mapper;

import com.kerjapro.review.dto.request.CreateReviewRequest;
import com.kerjapro.review.dto.response.ReviewDto;
import com.kerjapro.review.entity.Review;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    ReviewDto toDto(Review entity);

    @Mapping(target = "reviewerId",   ignore = true)
    @Mapping(target = "subProfileId", ignore = true)
    Review toEntity(CreateReviewRequest request);
}
