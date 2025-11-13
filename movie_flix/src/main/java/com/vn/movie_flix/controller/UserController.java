package com.vn.movie_flix.controller;

import com.vn.movie_flix.dto.response.ColumnRes;
import com.vn.movie_flix.model.Comment;
import com.vn.movie_flix.model.User;
import com.vn.movie_flix.service.CommentService;
import com.vn.movie_flix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CommentService commentService;

    @GetMapping
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "id,asc") String sort,
                            @RequestParam(required = false) String keyword) {

        String sortField = sort.split(",")[0];
        String sortOrder = sort.split(",")[1];
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortField));

        Page<User> userPage = userService.getUsers(keyword, pageable);

        List<ColumnRes> columns = new ArrayList<>();
        columns.add(new ColumnRes("id", "ID", true));
        columns.add(new ColumnRes("email", "Email", true));
        columns.add(new ColumnRes("role.name", "Vai trò", true));

        model.addAttribute("title", "Danh sách Người dùng");
        model.addAttribute("userPage", userPage);
        model.addAttribute("columns", columns);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);

        return "/admin/user/list";
    }

    @GetMapping("/{id}")
    public String userDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng với ID " + id);
            return "redirect:/admin/user";
        }

        List<Comment> comments = commentService.getCommentsByUserId(id);

        model.addAttribute("user", user);
        model.addAttribute("comments", comments);
        model.addAttribute("title", "Chi tiết Người dùng: " + user.getEmail());

        return "/admin/user/detail";
    }
}