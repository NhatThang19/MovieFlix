package com.vn.movie_flix.controller;

import com.vn.movie_flix.constant.EditMode;
import com.vn.movie_flix.dto.response.ColumnRes;
import com.vn.movie_flix.model.Actor;
import com.vn.movie_flix.model.Actor;
import com.vn.movie_flix.service.ActorService;
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
public class ActorController {
    private final ActorService actorService;

    @GetMapping("/admin/actor")
    public String showActorPage(
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

        Page<Actor> actorPage = actorService.findAll(keyword, pageable);

        List<ColumnRes> columns = new ArrayList<>();
        columns.add(new ColumnRes("id", "ID", true));
        columns.add(new ColumnRes("name", "Tên diễn viên", true));

        model.addAttribute("title", "Danh sách diễn viên");
        model.addAttribute("actorPage", actorPage);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("columns", columns);

        return "/admin/actor/list";
    }

    @GetMapping("/admin/actor/create")
    public String showCreatActorPage(Model model) {
        model.addAttribute("title", "Thêm diễn viên");
        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("actor", new Actor());
        return "/admin/actor/edit";
    }

    @GetMapping("/admin/actor/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Actor actor = actorService.findById(id);
        if (actor == null) {
            return "redirect:/admin/actor";
        }

        model.addAttribute("title", "Cập nhật diễn viên");
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("actor", actor);
        return "/admin/actor/edit";
    }

    @PostMapping("/admin/actor/save")
    public String saveActor(@Valid @ModelAttribute("actor") Actor actor,
                               BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            if (actor.getId() == null) {
                model.addAttribute("title", "Thêm diễn viên mới");
                model.addAttribute("editMode", EditMode.CREATE);
            } else {
                model.addAttribute("title", "Cập nhật diễn viên");
                model.addAttribute("editMode", EditMode.UPDATE);
            }
            return "/admin/actor/edit";
        }

        actorService.save(actor);

        return "redirect:/admin/actor";
    }

    @PostMapping("/admin/actor/delete/{id}")
    public String deleteActor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa diễn viên có ID " + id + " thành công!");
            actorService.deleteById(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi! Không thể xóa diễn viên. " + e.getMessage());
        }
        return "redirect:/admin/actor";
    }
}
