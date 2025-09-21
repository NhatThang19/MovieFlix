package com.vn.movie_flix.controller;

import com.vn.movie_flix.constant.EditMode;
import com.vn.movie_flix.dto.response.ColumnRes;
import com.vn.movie_flix.model.Country;
import com.vn.movie_flix.service.CountryService;
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
public class CountryController {
    private final CountryService countryService;

    @GetMapping("/admin/country")
    public String showCountryPage(
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

        Page<Country> countryPage = countryService.findAll(keyword, pageable);

        List<ColumnRes> columns = new ArrayList<>();
        columns.add(new ColumnRes("id", "ID", true));
        columns.add(new ColumnRes("name", "Tên quốc gia", true));

        model.addAttribute("title", "Danh sách quốc gia");
        model.addAttribute("countryPage", countryPage);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("columns", columns);

        return "/admin/country/list";
    }

    @GetMapping("/admin/country/create")
    public String showCreatCountryPage(Model model) {
        model.addAttribute("title", "Thêm quốc gia");
        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("country", new Country());
        return "/admin/country/edit";
    }

    @GetMapping("/admin/country/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Country country = countryService.findById(id);
        if (country == null) {
            return "redirect:/admin/country";
        }

        model.addAttribute("title", "Cập nhật quốc gia");
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("country", country);
        return "/admin/country/edit";
    }

    @PostMapping("/admin/country/save")
    public String saveCountry(@Valid @ModelAttribute("country") Country country,
                            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            if (country.getId() == null) {
                model.addAttribute("title", "Thêm quốc gia mới");
                model.addAttribute("editMode", EditMode.CREATE);
            } else {
                model.addAttribute("title", "Cập nhật quốc gia");
                model.addAttribute("editMode", EditMode.UPDATE);
            }
            return "/admin/country/edit";
        }

        countryService.save(country);

        return "redirect:/admin/country";
    }

    @PostMapping("/admin/country/delete/{id}")
    public String deleteCountry(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa quốc gia có ID " + id + " thành công!");
            countryService.deleteById(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi! Không thể xóa quốc gia. " + e.getMessage());
        }
        return "redirect:/admin/country";
    }
}
