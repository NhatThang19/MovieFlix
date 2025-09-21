package com.vn.movie_flix.controller;

import com.vn.movie_flix.constant.EditMode;
import com.vn.movie_flix.dto.response.ColumnRes;
import com.vn.movie_flix.model.Director;
import com.vn.movie_flix.service.DirectorService;
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
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/admin/director")
    public String showDirectorPage(
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

        Page<Director> directorPage = directorService.findAll(keyword, pageable);

        List<ColumnRes> columns = new ArrayList<>();
        columns.add(new ColumnRes("id", "ID", true));
        columns.add(new ColumnRes("name", "Tên đạo diễn", true));

        model.addAttribute("title", "Danh sách đạo diễn");
        model.addAttribute("directorPage", directorPage);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("columns", columns);

        return "/admin/director/list";
    }

    @GetMapping("/admin/director/create")
    public String showCreatDirectorPage(Model model) {
        model.addAttribute("title", "Thêm đạo diễn");
        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("director", new Director());
        return "/admin/director/edit";
    }

    @GetMapping("/admin/director/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Director director = directorService.findById(id);
        if (director == null) {
            return "redirect:/admin/director";
        }

        model.addAttribute("title", "Cập nhật đạo diễn");
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("director", director);
        return "/admin/director/edit";
    }

    @PostMapping("/admin/director/save")
    public String saveDirector(@Valid @ModelAttribute("director") Director director,
                               BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            if (director.getId() == null) {
                model.addAttribute("title", "Thêm đạo diễn mới");
                model.addAttribute("editMode", EditMode.CREATE);
            } else {
                model.addAttribute("title", "Cập nhật đạo diễn");
                model.addAttribute("editMode", EditMode.UPDATE);
            }
            return "/admin/director/edit";
        }

        directorService.save(director);

        return "redirect:/admin/director";
    }

    @PostMapping("/admin/director/delete/{id}")
    public String deleteDirector(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa đạo diễn có ID " + id + " thành công!");
            directorService.deleteById(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi! Không thể xóa đạo diễn. " + e.getMessage());
        }
        return "redirect:/admin/director";
    }
}
