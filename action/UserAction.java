package com.ning.nsfw.action;

import com.ning.Exception.UserException;
import com.ning.nsfw.entity.User;
import com.ning.nsfw.service.UserService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangn on 2017/3/29.
 */
@Controller
@RequestMapping("/nsfw")
public class UserAction {
    @Resource
    private UserService userService;

    //自定义类型转换器
    @InitBinder
    public void initBinder(WebDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    //显示所有User
    @RequestMapping("/showUsers")
    public String showUsers(Model model) throws Exception {
        List<User> usersList = userService.showUsers();
        model.addAttribute("usersList", usersList);
        return "/jsp/nsfw/user/listUI.jsp";
    }

    //根据id删除User
    @RequestMapping("/deleteById")
    public String deleteById(String id) throws Exception {
        if (id == null) {
            throw new UserException("查询失败：ID为空");
        }
        userService.deleteById(id);
        return "/jsp/nsfw/user/listUI.jsp";
    }

    //根据id查找User(查询用户,修改用户)
    @RequestMapping("/findUserById")
    public String findUserById(Model model, String id) throws Exception {
        if (id == null) {
            throw new UserException("查询失败：ID为空");
        }
        User user = userService.findUserById(id);
        model.addAttribute("userList", user);
        return "/jsp/nsfw/user/editUI.jsp";
    }

    //修改某用户
    @RequestMapping("/editUser")
    public String editUser(@RequestParam MultipartFile file, User user) throws Exception {
        if (user == null) {
            throw new UserException("修改失败：参数为空");
        }
        if (file == null) {
            throw new UserException("修改失败：未上传文件");
        }
        //文件上传目录
        String path = "D:/upload/";
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //通过UUID重构文件名
        String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //
        File newFile = new File(path + newFileName);
        //写入文件
        file.transferTo(newFile);
        //文件名封装到实体类
        user.setHeadImg(newFileName);
        userService.editUser(user);
        return "redirect:/nsfw/showUsersByLimit?start=0&size=10&pageNumber=1";
    }

    //新增用户
    @RequestMapping("/insertUser")
    public String insertUser(@RequestParam MultipartFile file, User user) throws Exception {
        if (user == null) {
            throw new UserException("新增失败：参数为空");
        }
        if (file == null) {
            throw new UserException("修改失败：未上传文件");
        }
        //文件上传目录
        String path = "D:/upload/";
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //通过UUID重构文件名
        String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //
        File newFile = new File(path + newFileName);
        //写入文件
        file.transferTo(newFile);
        //文件名封装到实体类
        user.setHeadImg(newFileName);

        userService.insertUser(user);
        return "redirect:/nsfw/showUsersByLimit?start=0&size=10&pageNumber=1";
    }

    //导出Excal
    @RequestMapping("/excalExport")
    public void excalExport(HttpServletResponse response) throws Exception {
        response.setContentType("application/x-execl");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String("用户列表.xls".getBytes(), "ISO-8859-1"));
        ServletOutputStream outputStream = response.getOutputStream();
        userService.excalExport(outputStream);
        outputStream.close();
    }

    //导入Excal
    @RequestMapping("/excalImport")
    public String excalImport(@RequestParam MultipartFile file) throws Exception {
        if (file == null) {
            throw new UserException("导入失败：未上传文件");
        }
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (!(originalFilename.matches("^.+\\.(?i)((xls)|(xlsx))$"))) {
            throw new UserException("导入失败：文件格式不正确");
        }
        userService.excalImport(file.getInputStream(), originalFilename);
        return "redirect:/nsfw/showUsersByLimit?start=0&size=10&pageNumber=1";
    }

    //分页查询
    @RequestMapping("/showUsersByLimit")
    public String showUsersByLimit(Model model,Integer start,Integer size,Integer pageNumber) throws Exception {
        if(pageNumber==null){
            pageNumber=0;
        }
        if(start==null||size==null){
            throw new UserException("查询失败：没有分页参数");
        }
        Map<String,Integer> map =new HashMap<String, Integer>();
        map.put("pstart",start);
        map.put("psize",size);
        List<User> usersList=userService.showUsersByLimit(map);
        model.addAttribute("pageNumber",pageNumber);
        model.addAttribute("usersList", usersList);
        model.addAttribute("usersSize",userService.showUsersSize());
        return "/jsp/nsfw/user/listUI.jsp";
    }

    //根据用户名查找User(查询用户,修改用户)
    @RequestMapping("/findUserByName")
    public String findUserByName(Model model, String name) throws Exception {
        List<User> usersList= userService.findUsersByName(name);
        model.addAttribute("pageNumber",1);
        model.addAttribute("usersList", usersList);
        model.addAttribute("usersSize",usersList.size());
        return "/jsp/nsfw/user/listUI.jsp";
    }
}
