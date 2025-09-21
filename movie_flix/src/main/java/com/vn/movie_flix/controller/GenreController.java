package com.vn.movie_flix.controller;

import com.vn.movie_flix.constant.EditMode;
import com.vn.movie_flix.dto.response.ColumnRes;
import com.vn.movie_flix.model.Genre;
import com.vn.movie_flix.service.GenreService;
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
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/admin/genre")
    public String showGenrePage(
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

        Page<Genre> genrePage = genreService.findAll(keyword, pageable);

        List<ColumnRes> columns = new ArrayList<>();
        columns.add(new ColumnRes("id", "ID", true));
        columns.add(new ColumnRes("name", "Tên thể loại", true));

        model.addAttribute("title", "Danh sách thể loại");
        model.addAttribute("genrePage", genrePage);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("columns", columns);

        return "/admin/genre/list";
    }

    @GetMapping("/admin/genre/create")
    public String showCreatGenrePage(Model model) {
        model.addAttribute("title", "Thêm thể loại");
        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("genre", new Genre());
        return "/admin/genre/edit";
    }

    @GetMapping("/admin/genre/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Genre genre = genreService.findById(id);
        if (genre == null) {
            return "redirect:/admin/genre";
        }

        model.addAttribute("title", "Cập nhật thể loại");
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("genre", genre);
        return "/admin/genre/edit";
    }

    @PostMapping("/admin/genre/save")
    public String saveGenre(@Valid @ModelAttribute("genre") Genre genre,
                            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            if (genre.getId() == null) {
                model.addAttribute("title", "Thêm thể loại mới");
                model.addAttribute("editMode", EditMode.CREATE);
            } else {
                model.addAttribute("title", "Cập nhật thể loại");
                model.addAttribute("editMode", EditMode.UPDATE);
            }
            return "/admin/genre/edit";
        }

        genreService.save(genre);

        return "redirect:/admin/genre";
    }

    @PostMapping("/admin/genre/delete/{id}")
    public String deleteGenre(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thể loại có ID " + id + " thành công!");
            genreService.deleteById(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi! Không thể xóa thể loại. " + e.getMessage());
        }
        return "redirect:/admin/genre";
    }
}
