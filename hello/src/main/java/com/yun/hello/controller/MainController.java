package com.yun.hello.controller;

import java.util.ArrayList;
import java.util.List;

import com.yun.hello.domain.Authority;
import com.yun.hello.domain.User;
import com.yun.hello.service.AuthorityService;
import com.yun.hello.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 主页控制器.
 *
 */
@Controller
public class MainController {


	/*
	 * 普通用户，即博主的权限ID
	 */
	private static final Long ROLE_USER_AUTHORITY_ID = 2L;

	/**
	 * 角色处理service
	 */
	@Autowired
	private static final AuthorityService authorityService = null;

	@Autowired
	private static final UserService userService = null;
	
	/**
	 * http://localhost:8080，不带任何后缀时访问首页
	 * @return
	 */
	@GetMapping("/")
	public String root() {
		return "redirect:/u/admin/blogs";
	}
	
	@GetMapping("/index")
	public String index() {
		return "index";
	}

	@GetMapping("/photo")
	public String photo() {
		return "/photo/index";
	}

    @GetMapping("/photo2")
    public String photo2() {
        return "/photo/index4";
    }

    @GetMapping("/uploadphoto")
    public String uploadphoto() {
        return "/photo/uploadphoto";
    }

	/**
	 * 获取登录界面
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/search")
	public String search() {
		return "search";
	}

	@PostMapping("/register")
	public String register(User user) {

		//1. 给要注册的用户设置为普通用户的角色
		List<Authority> authorities = new ArrayList<>();
		authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
		user.setAuthorities(authorities);

		//2. 注册用户+保存角色
		// userService.registerUser(user);

		//3. 注册完用户后需要跳转回登陆页面
		return "login";
	}
}
