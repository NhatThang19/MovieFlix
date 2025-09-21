package com.vn.movie_flix.controller;

import com.vn.movie_flix.dto.request.UserRegisterReq;
import com.vn.movie_flix.repository.RoleRepository;
import com.vn.movie_flix.service.AuthService;
import com.vn.movie_flix.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        return "client/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        if (!model.containsAttribute("registerReq")) {
            model.addAttribute("registerReq", new UserRegisterReq());
        }

        return "client/register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registerReq") UserRegisterReq registerReq,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {

        if (!registerReq.getPassword().isEmpty() && !registerReq.getConfirmPassword().isEmpty() &&
                !registerReq.getPassword().equals(registerReq.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "MatchPassword.registerReq.confirmPassword",
                    "Mật khẩu xác nhận không khớp.");
        }

        if (registerReq.getEmail() != null && !registerReq.getEmail().isEmpty()
                && userService.existsByEmail(registerReq.getEmail())) {
            bindingResult.rejectValue("email", "Exist.registerReq.email", "Địa chỉ email này đã được sử dụng.");
        }

        if (bindingResult.hasErrors()) {
            return "client/register";
        }

        try {
            authService.register(registerReq);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/register";
        }
    }
}
