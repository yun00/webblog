package com.yun.hello.controller;

import com.yun.hello.domain.Blog;
import com.yun.hello.domain.User;
import com.yun.hello.service.BlogService;
import com.yun.hello.service.UserService;
import com.yun.hello.util.ConstraintViolationExceptionHandler;
import com.yun.hello.vo.Response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;

/**
 * 用户主页空间控制器.
 * 
 *
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BlogService blogService;
	
	@Value("${file.server.url}")
	private String fileServerUrl;
	
	@GetMapping("/{username}")
	public String userSpace(@PathVariable("username") String username, Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return "redirect:/u/" + username + "/blogs";
	}

	/**
	 * 获取用户个人信息
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)") 
	public ModelAndView profile(@PathVariable("username") String username, Model model) {

		//1. 根据用户名获取用户信息
		User user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		model.addAttribute("fileServerUrl",fileServerUrl);
		return new ModelAndView("userspace/profile", "userModel", model);
	}
 
	/**
	 * 保存个人设置
	 * @param user
	 *
	 * @return
	 */
	@PostMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)") 
	public String saveProfile(@PathVariable("username") String username,User user) {
		
		//1. 根据传入的用户信息中的用户id获取已保存的原始用户信息
		User originalUser = userService.getUserById(user.getId());
		
		//2. 根据页面填写的参数保存用户的email和name信息
		originalUser.setEmail(user.getEmail());
		originalUser.setName(user.getName());
		
		//3. 判断密码是否做了变更,如何密码变更,需要重新保存和编码
		String rawPassword = originalUser.getPassword();
		PasswordEncoder  encoder = new BCryptPasswordEncoder();
		String encodePasswd = encoder.encode(user.getPassword());
		
		//4. 当前页面输入加密后的密码是否和当前已保存的加密后的密码一致，如果不一致先对最新填写的密码进行加密并保存
		boolean isMatch = encoder.matches(rawPassword, encodePasswd);
		if (!isMatch) {
			originalUser.setEncodePassword(user.getPassword());
		}

		//5. 保存用户数据
		userService.saveUser(originalUser);
		
		//6. 保存完用户信息重定向到"/{username}/profile"请求上去，即还是当前的个人设置页面
		return "redirect:/u/" + username + "/profile";
	}
	
	/**
	 * 获取编辑头像的界面
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)") 
	public ModelAndView avatar(@PathVariable("username") String username, Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return new ModelAndView("userspace/avatar", "userModel", model);
	}
	
	
	/**
	 * 保存头像
	 * @param username
	 * @return
	 */
	@PostMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)") 
	public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, User user) {
		
		//1. 获取图片上传到文件服务器上后返回的地址
		String avatarUrl = user.getAvatar();
		
		//2. 保存图片地址
		User originalUser = userService.getUserById(user.getId());
		originalUser.setAvatar(avatarUrl);
		userService.saveUser(originalUser);
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", ""));
	}

	/**
	 * 获取指定用户的所有博客
	 * @param username
	 * @param order
	 * @param category
	 * @param keyword
	 * @param async
	 * @param pageIndex
	 * @param pageSize
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/blogs")
	public String listBlogsByOrder(@PathVariable("username") String username,
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="category",required=false ) Long category,
			@RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
			@RequestParam(value="async",required=false) boolean async,
			@RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
			@RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
			Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		
		if (category != null) {
			
			System.out.print("category:" +category );
			System.out.print("selflink:" + "redirect:/u/"+ username +"/blogs?category="+category);
			return "/u";
			
		}
		
		Page<Blog> page = null;
		if (order.equals("hot")) { // 最热查询
			Sort sort = new Sort(Direction.DESC,"reading","comments","likes"); 
			Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
			page = blogService.listBlogsByTitleLikeAndSort(user, keyword, pageable);
		}
		if (order.equals("new")) { // 最新查询
			Pageable pageable = new PageRequest(pageIndex, pageSize);
			page = blogService.listBlogsByTitleLike(user, keyword, pageable);
		}
		
		List<Blog> list = page.getContent();	// 当前所在页面数据列表

		model.addAttribute("order", order);
		model.addAttribute("page", page);
		model.addAttribute("blogList", list); // 当前用户的博客列表
		return (async==true?"userspace/u :: #mainContainerRepleace":"userspace/u");
	}

    /**
     * 获取指定用户的指定博客
     * @param id 博客id
     * @return
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username,@PathVariable("id") Long id, Model model) {

        // 1. 根据博客ID获取博客对象
        Blog blog = blogService.getBlogById(id);

        // 2. 放入模型中
        model.addAttribute("blogModel",blog); // 当前博客对象
        return "/userspace/blog";
    }

	/**
	 * 获取博客的新增页面
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/blogs/edit/new")
	public ModelAndView createBlog(Model model) {
	    //1. 新增博客，实体为空
	    Blog blog = new Blog(null, null, null);

	    //2. 由于前端页面中需要知道当前用户是谁,通过Authentication.name来获取
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("blog", blog);
		model.addAttribute("authentication",authentication);

		//3. 跳转到博客编辑页面，新增和编辑公用一个页面
		return new ModelAndView("/userspace/blogedit", "blogModel", model);
	}

	@GetMapping("/{username}/blogs/edit/{id}")
	public ModelAndView getEditBlog(Model model,@PathVariable("id") Long id) {
        // 1. 根据博客ID获取博客对象
        Blog blog = blogService.getBlogById(id);

		//2. 由于前端页面中需要知道当前用户是谁,通过Authentication.name来获取
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("blog", blog);
		model.addAttribute("authentication",authentication);

		//3. 跳转到博客编辑页面，新增和编辑公用一个页面
		return new ModelAndView("/userspace/blogedit", "blogModel", model);
	}

    /**
     * 保存博客
     * @param username
     * @param blog
     * @return
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {

        try {

            //1. 编辑博客,更新标题，内容和摘要
            if (blog.getId()!=null) {
                Blog orignalBlog = blogService.getBlogById(blog.getId());
                orignalBlog.setTitle(blog.getTitle());
                orignalBlog.setContent(blog.getContent());
                orignalBlog.setSummary(blog.getSummary());
                blogService.saveBlog(orignalBlog);
            } else { //2.新增博客，关联用户，保存后数据库自增生成博客id
                User user = (User)userDetailsService.loadUserByUsername(username);
                blog.setUser(user);
                blogService.saveBlog(blog);
            }

        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        // 保存博客后重定向到这条博客的显示页面
        // String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }
}
