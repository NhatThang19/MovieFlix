package com.vn.movie_flix.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDto {
    private long totalFilms;
    private long totalGenres;
    private long totalActors;
    private long totalCountries;
    private long totalUsers;
}
