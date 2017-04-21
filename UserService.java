package com.ning.nsfw.service;

import com.ning.nsfw.entity.User;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by wangn on 2017/3/29.
 */
public interface UserService {
    //显示所有的用户
    public List<User> showUsers() throws Exception;
    //根据id查找用户
    public User findUserById(Serializable id) throws Exception;
    //根据id删除用户
    public void deleteById(Serializable id) throws Exception;
    //增加一个用户
    public void insertUser(User user) throws Exception;
    //修改某用户
    public void editUser(User user) throws Exception;
    //导出Excal
    public void excalExport(ServletOutputStream outputStream) throws Exception;
    //导入Excal
    public void excalImport(InputStream inputStream,String filename) throws Exception;
    //分页查询
    public List<User> showUsersByLimit(Map<String,Integer> map) throws Exception;
    //用户数量
    public int showUsersSize() throws Exception;
    //根据用户名查找用户
    public List<User> findUsersByName(String name) throws Exception;
}
