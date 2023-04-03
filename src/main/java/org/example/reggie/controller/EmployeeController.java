package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;
import org.example.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request  request
     * @param employee 员工
     * @return 员工
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        log.info("员工登录");

        // 密码加密(MD5)
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 查询数据库
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(employeeLambdaQueryWrapper);

        // 判断
        if (emp == null) {
            return R.error("用户名或密码错误");
        }

        // 判断密码
        if (!emp.getPassword().equals(password)) {
            return R.error("用户名或密码错误");
        }

        // 员工状态
        if (emp.getStatus() == 0) {
            return R.error("该员工已被禁用");
        }

        // 登录成功
        request.getSession().setAttribute("employee", emp);
        log.info("登录成功：{}", emp.toString());

        // 返回
        return R.success(emp);
    }


    /**
     * 员工退出
     *
     * @param request request
     * @return 员工
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {

        log.info("员工退出");

        // 清除session
        request.getSession().removeAttribute("employee");

        // 返回
        return R.success("退出成功");
    }


    /**
     * 新增员工
     *
     * @param request  request
     * @param employee 员工
     * @return 成功信息
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {

        log.info("新增员工：{}", employee.toString());

        // 密码加密(MD5)
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 保存
        employeeService.save(employee);

        // 返回
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     *
     * @param page 页码
     * @param pageSize 每页显示数量
     * @param name 员工姓名
     * @return 员工信息列表
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize,String name) {

        log.info("员工信息分页查询：page={},pageSize={},name={}", page, pageSize,name);

        // 构造分页构造器
        Page<Employee> employeePage = new Page<>(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件
        employeeLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName, name);

        // 添加排序条件
        employeeLambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        // 查询
        employeeService.page(employeePage, employeeLambdaQueryWrapper);

        // 返回
        log.info("员工信息分页查询成功");
        return R.success(employeePage);
    }

    /**
     * 修改员工
     *
     * @param request  request
     * @param employee 员工
     * @return 成功信息
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {

        log.info("修改员工：{}", employee.toString());

        // 设置更新时间
        employee.setUpdateTime(LocalDateTime.now());

        // 设置更新人
        employee.setUpdateUser(((Employee) request.getSession().getAttribute("employee")).getId());

        // 保存
        employeeService.updateById(employee);

        // 返回
        return R.success("修改员工成功");
    }


    /**
     * 查询员工
     * @param id 员工id
     * @return 员工
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {

        log.info("查询员工：{}", id);

        // 查询
        Employee employee = employeeService.getById(id);

        // 返回
        if (employee != null) {
            return R.success(employee);
        } else {
            return R.error("没有该员工");
        }
    }
}