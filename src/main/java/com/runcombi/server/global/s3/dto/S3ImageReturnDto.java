package com.runcombi.server.global.s3.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class S3ImageReturnDto {
    private String imageUrl;
    private String imageKey;
}
