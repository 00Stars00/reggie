package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.reggie.common.CustomException;
import org.example.reggie.common.R;
import org.example.reggie.entity.User;
import org.example.reggie.service.UserService;
import org.example.reggie.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String MyFrom;


    /**
     * 发送验证码
     *
     * @param user 用户信息
     * @return 发送结果
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {

        // 1. 校验手机号是否已经注册
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {

            // 2. 生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码为: {}", code);

            // 3. 发送验证码
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MyFrom);
            message.setTo(phone);
            message.setSubject("瑞吉外卖登录验证码");
            message.setText("您的验证码为: " + code);

            try {
                mailSender.send(message);
            } catch (Exception e) {
                log.error("发送验证码失败: {}", e.getMessage());
                throw new CustomException("发送验证码失败");
            }
            
            // 4. 将验证码存入redis
            redisTemplate.opsForValue().set(phone, code,5, TimeUnit.MINUTES);

            // 5. 返回结果
            log.info("发送成功");
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }


    /**
     * 移动端登录
     *
     * @param map 用户信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {

        log.info("map: {}", map);

        // 获取邮箱地址
        String phone = map.get("phone");

        // 获取验证码
        String code = map.get("code");
        
        // 获取redis中的验证码
        String redisCode = (String) redisTemplate.opsForValue().get(phone);

        // 校验验证码
        if (redisCode != null && redisCode.equals(code)) {
            // 校验用户是否存在
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(userLambdaQueryWrapper);
            if (user == null) {
                // 用户不存在, 注册用户
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            session.setAttribute("user", user);

            // 如果登录成功, 将验证码从redis中删除
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");
    }

}
