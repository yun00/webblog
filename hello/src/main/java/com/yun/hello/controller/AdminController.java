package com.yun.hello.controller;

import java.util.ArrayList;
import java.util.List;

import com.yun.hello.vo.Menu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * 用户控制器.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @date 2017年2月26日
 */
@Controller
@RequestMapping("/admins")
public class AdminController {

	/**
	 * 获取后台管理主页面
	 * @return
	 */
	@GetMapping
	public ModelAndView listUsers(Model model) {
		List<Menu> list = new ArrayList<>();
		list.add(new Menu("用户管理", "/users"));//点击菜单中的用户管理，访问/users请求
		list.add(new Menu("博客管理", "/blogs"));
		list.add(new Menu("评论管理", "/commits"));
		model.addAttribute("list", list);
		return new ModelAndView("admins/index", "model", model);
	}


}
