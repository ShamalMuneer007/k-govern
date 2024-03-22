package org.kerala.kgovern.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewsDto {
    private MultipartFile file;
    private String newsHeading;
    private String newsContent;
    private String expiryDate;
}
