package com.vn.movie_flix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnRes {
    private String field;
    private String label;
    private boolean sortable;
}
