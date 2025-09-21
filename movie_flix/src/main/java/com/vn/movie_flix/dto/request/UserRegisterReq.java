package com.vn.movie_flix.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterReq {
    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
