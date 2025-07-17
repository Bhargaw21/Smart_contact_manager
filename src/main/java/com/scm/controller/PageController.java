package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.message;
import com.scm.helpers.messageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ Required for encoding password

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
public String home(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().toString().equals("anonymousUser")) {
        return "redirect:/login";
    }

    return "Home";
}

    @GetMapping("/about")
    public String about() {
        System.out.println("page loading....");
        return "about";
    }

    @GetMapping("/services")
    public String Servicespage() {
        System.out.println("services page loading....");
        return "services";
    }

    @GetMapping("/contact")
    public String contactpage() {
        return "contact";
    }

    @GetMapping("/login")
public String loginpage(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().toString().equals("anonymousUser")) {
        // Redirect to dashboard if already authenticated
        return "redirect:/user/profile";
    }
    return "login";
}


@GetMapping("/register")
public String registerpage(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().toString().equals("anonymousUser")) {
        return "redirect:/user/profile";  // already logged in
    }

    UserForm userForm = new UserForm();
    model.addAttribute("userForm", userForm);
    return "register";
}

    // processing registration
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult, HttpSession session) {
        System.out.println("processing registration");
        System.out.println(userForm);

        // Validate form data
        if (rBindingResult.hasErrors()) {
            return "register";
        }

        // Create user from form
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setAbout(userForm.getAbout());

        // ✅ Encode password before saving
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));

        user.setPhoneNumber(userForm.getPhoneNumber());

        // ✅ Enable user for login
        user.setEnabled(true);

        user.setProfilepic("");

        // Save to DB
        User savedUser = userService.saveUser(user);
        System.out.println("user saved");

        // Add success message
        message Message = message.builder()
                .content("Registration Successful")
                .type(messageType.green)
                .build();

        session.setAttribute("Message", Message);

        // Redirect to login instead of register
        return "redirect:/login";  // ✅ Go to login after registration
    }
}
