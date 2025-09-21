package com.vn.movie_flix.controller;

import com.vn.movie_flix.constant.EditMode;
import com.vn.movie_flix.dto.response.ColumnRes;
import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.model.Film;
import com.vn.movie_flix.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final GenreService genreService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final CountryService countryService;

    @ModelAttribute
    public void editAttributes(Model model) {
        model.addAttribute("genres", genreService.getGenresAsMap());
        model.addAttribute("actors", actorService.getActorsAsMap());
        model.addAttribute("directors", directorService.getDirectorsAsMap());
        model.addAttribute("countries", countryService.getCountriesAsMap());
    }

    @GetMapping("/admin/film")
    public String showFilmPage(
            Model model,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDir = sortParams.length > 1 ? sortParams[1] : "asc";
        Sort sortOrder = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);

        Pageable pageable = PageRequest.of(page - 1, size, sortOrder);

        Page<Film> filmPage = filmService.findAll(keyword, pageable);

        List<ColumnRes> columns = new ArrayList<>();
        columns.add(new ColumnRes("id", "ID", true));
        columns.add(new ColumnRes("title", "Tên phim", true));
        columns.add(new ColumnRes("director", "Đạo diễn", true));
        columns.add(new ColumnRes("duration", "Thời lượng", true));
        columns.add(new ColumnRes("releaseYear", "Năm phát hành", true));
        columns.add(new ColumnRes("country", "Quốc gia", true));

        model.addAttribute("title", "Danh sách phim");
        model.addAttribute("filmPage", filmPage);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("columns", columns);

        return "/admin/film/list";
    }

    @GetMapping("/admin/film/create")
    public String showCreatFilmPage(Model model) {
        model.addAttribute("title", "Thêm phim");
        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("film", new Film());
        return "/admin/film/edit";
    }

    @GetMapping("/admin/film/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Film film = filmService.findById(id);
        if (film == null) {
            return "redirect:/admin/film";
        }

        model.addAttribute("title", "Cập nhật phim");
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("film", film);
        return "/admin/film/edit";
    }

    @PostMapping("/admin/film/save")
    public String saveFilm(@Valid @ModelAttribute("film") Film film,
                            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            if (film.getId() == null) {
                model.addAttribute("title", "Thêm phim mới");
                model.addAttribute("editMode", EditMode.CREATE);
            } else {
                model.addAttribute("title", "Cập nhật phim");
                model.addAttribute("editMode", EditMode.UPDATE);
            }
            return "/admin/film/edit";
        }

        filmService.save(film);

        return "redirect:/admin/film";
    }

    @PostMapping("/admin/film/delete/{id}")
    public String deleteGenre(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phim có ID " + id + " thành công!");
            filmService.deleteById(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi! Không thể xóa phim. " + e.getMessage());
        }
        return "redirect:/admin/film";
    }
}
