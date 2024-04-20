package com.root.authservice.dto.out.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendingTimeRecordUpdateListResponseDTO {
    int page;
    int perPage;
    long totalElements;
    List<PendingTimeRecordActionResponseDTO> list;
}
