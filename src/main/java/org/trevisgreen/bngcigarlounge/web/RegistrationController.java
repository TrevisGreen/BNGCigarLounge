/*
 * The MIT License
 *
 * Copyright 2015 Trevis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.trevisgreen.bngcigarlounge.web;

import java.util.Arrays;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.trevisgreen.bngcigarlounge.model.Message;
import org.trevisgreen.bngcigarlounge.model.User;
import org.trevisgreen.bngcigarlounge.service.MessageService;
import org.trevisgreen.bngcigarlounge.service.UserService;
import org.trevisgreen.bngcigarlounge.utils.Constants;

/**
 *
 * @author Trevis
 */
@Controller
public class RegistrationController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    protected AuthenticationManager authenticationManager;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    MessageService messageService;

    @RequestMapping(value = "/user/register", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "user/registration";
    }
    
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public String registerUserAccount(@Valid User user, BindingResult bindingResult, HttpServletRequest request, WebRequest webRequest, HttpSession session) {
        log.debug("Registering user {}", user.getUsername());
        String back = "user/registration";
        if (bindingResult.hasErrors()) {
            log.warn("Could not register user {}", bindingResult.getAllErrors());
            return back;
        }
        
        try {
            User u = userService.get(user.getUsername());
            if(u == null) {
                String password;
                if (StringUtils.isNotBlank(user.getPassword())) {
                    password = user.getPassword();
                } else {
                    password = Arrays.toString(KeyGenerators.secureRandom().generateKey());
                    user.setPassword(password);
                }
                user.addRole(userService.getRole("ROLE_USER"));
                user = userService.create(user);
                
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
                token.setDetails(new WebAuthenticationDetails(request));
                Authentication authenticatedUser = authenticationManager.authenticate(token);  
                
                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
                
                Message signup = messageService.get(Constants.SIGN_UP);
                MimeMessage message = mailSender.createMimeMessage();
                InternetAddress[] addresses = {new InternetAddress("B&G Cigar Lounge <bngcigarlounge@gmail.com>")};
                message.addFrom(addresses);
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(user.getUsername());
                helper.setSubject(signup.getSubject());
                String content = signup.getContent();
                content = content.replaceAll("@@NAME@@", user.getFirstName());
                content = content.replaceAll("@@USERNAME@@", user.getUsername());
                content = content.replaceAll("@@PASSWORD@@", password);

                helper.setText(content, true);
                mailSender.send(message);
            } else {
                log.warn("User already exists", user.getUsername());
                bindingResult.rejectValue("username", "user.email.already.exists");
                return back;
            }
            
            return "redirect:/event/create";
        } catch (Exception e) {
            log.error("Could not register user", e);
            return back;
        }
    }
}
