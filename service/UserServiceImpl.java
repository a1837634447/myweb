package com.ning.nsfw.service.impl;

import com.ning.Exception.UserException;
import com.ning.nsfw.dao.UserDao;
import com.ning.nsfw.entity.User;
import com.ning.nsfw.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wangn on 2017/3/29.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    public List<User> showUsers() throws Exception {
        return userDao.showUsers();
    }

    public User findUserById(Serializable id) throws Exception {
        return userDao.findUserById(id);
    }

    public void deleteById(Serializable id) throws Exception {
        userDao.deleteById(id);
    }

    public void insertUser(User user) throws Exception {
        if (user.getId() != null) {
            throw new UserException("新增失败：id已经有值");
        }
        user.setId(UUID.randomUUID().toString());
        userDao.insertUser(user);
    }

    public void editUser(User user) throws Exception {
        userDao.editUser(user);
    }

    public void excalExport(ServletOutputStream outputStream) throws Exception {
        //用户实体集合
        List<User> usersList = userDao.showUsers();
        //创建工作薄
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        //创建工作表
        HSSFSheet sheet1 = hssfWorkbook.createSheet("用户列表");
        //创建行
        HSSFRow row1 = sheet1.createRow(0);
        //创建单元格
        HSSFCell cell1 = row1.createCell(0);
        //合并单元格
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 3, 0, 4);
        sheet1.addMergedRegion(cellRangeAddress);
        //创建单元格样式
        HSSFCellStyle hssfCellStyle = hssfWorkbook.createCellStyle();
        //单元格内容水平居中
        hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        //单元格垂直居中
        hssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置合并的单元格样式
        cell1.setCellStyle(hssfCellStyle);
        //创建字体样式
        HSSFFont hssfFont = hssfWorkbook.createFont();
        //设置为粗体
        hssfFont.setBold(true);
        //设置字体样式
        hssfCellStyle.setFont(hssfFont);
        //设置合并的单元格内容
        cell1.setCellValue("用户列表");
        cell1.setCellStyle(hssfCellStyle);
        //设置列头
        HSSFRow row2 = sheet1.createRow(4);
        HSSFCell cell2 = row2.createCell(0);
        cell2.setCellStyle(hssfCellStyle);
        cell2.setCellValue("用户名");
        HSSFCell cell3 = row2.createCell(1);
        cell3.setCellStyle(hssfCellStyle);
        cell3.setCellValue("账户");
        HSSFCell cell4 = row2.createCell(2);
        cell4.setCellStyle(hssfCellStyle);
        cell4.setCellValue("所属部门");
        HSSFCell cell5 = row2.createCell(3);
        cell5.setCellStyle(hssfCellStyle);
        cell5.setCellValue("性别");
        HSSFCell cell6 = row2.createCell(4);
        cell6.setCellStyle(hssfCellStyle);
        cell6.setCellValue("电子邮箱");
        //遍历
        HSSFCellStyle hssfCellStyle2 = hssfWorkbook.createCellStyle();
        hssfCellStyle2.setAlignment(HorizontalAlignment.CENTER);
        hssfCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        Iterator<User> iterator = usersList.iterator();
        int i = 5;
        while (iterator.hasNext()) {
            HSSFRow row3 = sheet1.createRow(i);
            User user = iterator.next();

            HSSFCell cell01 = row3.createCell(0);
            cell01.setCellStyle(hssfCellStyle2);
            cell01.setCellValue(user.getName());

            HSSFCell cell02 = row3.createCell(1);
            cell02.setCellStyle(hssfCellStyle2);
            cell02.setCellValue(user.getAccount());

            HSSFCell cell03 = row3.createCell(2);
            cell03.setCellStyle(hssfCellStyle2);
            cell03.setCellValue(user.getDept());

            HSSFCell cell04 = row3.createCell(3);
            cell04.setCellStyle(hssfCellStyle2);
            cell04.setCellValue(user.getGender() ? "男" : "女");

            HSSFCell cell05 = row3.createCell(4);
            cell05.setCellStyle(hssfCellStyle2);
            cell05.setCellValue(user.getEmail());

            i++;
        }
        hssfWorkbook.write(outputStream);

    }

    public void excalImport(InputStream inputStream, String filename) throws Exception {
        boolean is03Excel = filename.matches("^.+\\.(?i)(xls)$");
        try {
        //1、读取工作簿
        Workbook workbook = is03Excel ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
        //2、读取工作表
        Sheet sheet = workbook.getSheetAt(0);
        //3、读取行
        if (sheet.getPhysicalNumberOfRows() > 5) {
            User user = null;
            for (int k = 5; k < sheet.getPhysicalNumberOfRows(); k++) {
                //4、读取单元格
                Row row = sheet.getRow(k);
                row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                user = new User();
                //用户名
                Cell cell0 = row.getCell(0);
                user.setName(cell0.getStringCellValue());
                //帐号
                Cell cell1 = row.getCell(1);
                user.setAccount(cell1.getStringCellValue());
                //所属部门
                Cell cell2 = row.getCell(2);
                user.setDept(cell2.getStringCellValue());
                //性别
                Cell cell3 = row.getCell(3);
                user.setGender(cell3.getStringCellValue().equals("男"));
                //电子邮箱
                Cell cell5 = row.getCell(4);
                user.setEmail(cell5.getStringCellValue());
                //默认用户密码为 123456
                user.setPassword("123456");
                //默认用户状态为 有效
                user.setState(true);
                //5、保存用户
                this.insertUser(user);
            }
        }
        workbook.close();
        inputStream.close();
        }catch (Exception e){
            throw new UserException("导入失败：请检查Excel内容格式是否正确");
        }
    }

    public List<User> showUsersByLimit(Map<String,Integer> map) throws Exception {
        return userDao.showUsersByLimit(map);
    }

    public int showUsersSize() throws Exception{
        return userDao.showUsersSize();
    }
    public List<User> findUsersByName(String name) throws Exception{
        return userDao.findUsersByName(name);
    }
}
