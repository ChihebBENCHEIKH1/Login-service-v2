package com.spring.social.controller;


import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.spring.social.dto.JwtLogin;
import com.spring.social.dto.LoginResponse;
import com.spring.social.dto.TokenDto;
import com.spring.social.model.Role;
import com.spring.social.model.User;
import com.spring.social.service.RoleService;
import com.spring.social.service.TokenService;
import com.spring.social.service.UserService;

// http://localhost:8080
@RestController
@RequestMapping("/social")
@CrossOrigin("http://localhost:4200")
//http://localhost:8080/social
public class SocialController {

    private UserService userService;

    private RoleService roleService;

    private TokenService tokenService;

    private PasswordEncoder passwordEncoder;

    private String email;


    @Value("${google.id}")
    private String idClient;

    @Value("${mySecret.password}")
    private String password;

    @Autowired
    public SocialController(UserService userService,RoleService roleService,TokenService tokenService,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    //http://localhost:8080/social/google
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody TokenDto tokenDto) throws Exception {
        System.out.println("pass " + password);
        NetHttpTransport transport = new NetHttpTransport();
        JacksonFactory factory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder ver =
                new GoogleIdTokenVerifier.Builder(transport,factory)
                        .setAudience(Collections.singleton(idClient));
        GoogleIdToken googleIdToken = GoogleIdToken.parse(ver.getJsonFactory(),tokenDto.getToken());
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        email = payload.getEmail();
        User user = new User();
        if(userService.ifEmailExist(email)){
            user = userService.getUserByMail(email);
        } 
        ///////////////////////////
        JwtLogin jwtLogin = new JwtLogin();
        jwtLogin.setEmail(user.getEmail());
        jwtLogin.setPassword(password);
        ///////////////////////////

        return new ResponseEntity<LoginResponse>(tokenService.login(jwtLogin), HttpStatus.OK);
    }
    
    @PostMapping("/user/addSupervisor")
    public User createSupervisor(@RequestBody String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Role role=new Role();
        role.setId(null);
        role.setNameRole("Supervisor");
        roleService.saveRole(role);
        user.getRoles().add(role);
        return userService.saveUser(user);
    }
    @PostMapping("/user/add-manager")
    public User createManager(@RequestBody String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Role role=new Role();
        role.setId(null);
        role.setNameRole("Manager");
        user.getRoles().add(role);
        return userService.saveUser(user);
    }
    @PostMapping("user/role")
    public String userRole(@RequestBody String email)
    {
    	User user=userService.getUserByMail(email);
    	Set<Role> role=user.getRoles();
    	Role firstElement=new Role();
    	Iterator<Role> iterator = role.iterator();
    	if (iterator.hasNext()) {
    	     firstElement = iterator.next();
    	}
    	return firstElement.getNameRole();
    }

    //http://localhost:8080/social/facebook
    @PostMapping("/facebook")
    public ResponseEntity<LoginResponse> loginWithFacebook(@RequestBody TokenDto tokenDto) throws Exception {
        Facebook facebook = new FacebookTemplate(tokenDto.getToken());
        String [] data = {"email"};
        org.springframework.social.facebook.api.User user = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class,data);

        email = user.getEmail();
        User userFace = new User();
        if(userService.ifEmailExist(email)){
            userFace = userService.getUserByMail(email);
        }
        ///////////////////////////
        JwtLogin jwtLogin = new JwtLogin();
        jwtLogin.setEmail(user.getEmail());
        jwtLogin.setPassword(password);
        ///////////////////////////

        return new ResponseEntity<LoginResponse>(tokenService.login(jwtLogin), HttpStatus.OK);
    }


}
