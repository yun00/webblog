package com.yun.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.yun.hello.domain.User;
import com.yun.hello.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * 查询所用用户
	 * @param model
	 * @return
	 */
	@GetMapping
	public ModelAndView list(Model model) {
		model.addAttribute("userList", userRepository.listUser());
		model.addAttribute("title","用户管理");
		return new ModelAndView("users/list","userModel",model);
	}
	
	/**
	 * 根据id来查询用户 
	 * @param model
	 * @return
	 */
	@GetMapping("{id}")
	public ModelAndView view(@PathVariable("id") Long id,Model model) {
		User user = userRepository.getUserById(id);
		
		model.addAttribute("user", user);
		model.addAttribute("title","查看用户");
		return new ModelAndView("users/view","userModel",model);
	}
	
	/**
	 * 获取创建表单页面 
	 * @param model
	 * @return
	 */
	@GetMapping("/form")
	public ModelAndView createForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("title","创建用户");
		return new ModelAndView("users/form","userModel",model);
	}
	
	/**
	 * 获取创建表单页面 
	 * @param model
	 * @return
	 */
	@PostMapping
	public ModelAndView saveOrUpdateUser(User user,Model model) {
		user = userRepository.saveOrUpdateUser(user);
		model.addAttribute("user",user);
		model.addAttribute("title","创建用户");
		return new ModelAndView("users/form","userModel",model);
	}
}