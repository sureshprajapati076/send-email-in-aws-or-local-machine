package com.example.demo.controllers;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.model.EmailDto;
import com.example.demo.model.JwtRequest;
import com.example.demo.model.JwtResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
public class JwtAuthenticationController {


    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private JwtUserDetailsService userDetailsService;
    private UserRepository userService;


    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService userDetailsService, UserRepository userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Autowired
    private JavaMailSender javaMailSender;

    @PostMapping("/testemail")
    public String emailSend(@RequestBody EmailDto emailDto) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(emailDto.getEmail());
        msg.setSubject("EMAIL TESTING IN AWS FARGATE");
        msg.setText(emailDto.getMessage());

        javaMailSender.send(msg);

        return "MESSAGE SENT";

    }


    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }




    @GetMapping("/public")
    public String publicPage() {
        return "PUBLIC ONLY";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody User user) throws Exception {

        if (userService.findByEmail(user.getEmail()) == null) {

            return ResponseEntity.ok(userDetailsService.save(user));
        } else {

            return new ResponseEntity<String>("{\"message\":\"Email Already Used\"}", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/authenticated")
    public String getUserDetail() {

        return "Only Authenticated Allowed";

    }

    @PostMapping("/update-user")
    public User updateUser(@RequestBody User user, HttpServletRequest request) {
        String username = jwtTokenUtil.getUsernameFromToken(request.getHeader("Authorization").substring(7));

        User current = userService.findByEmail(username);

        current.setName(user.getName());
        current.setAddress(user.getAddress());
        return userService.save(current);

    }

    @GetMapping("/checkifadmin")
    public ResponseEntity<?> testDecodeJWT(HttpServletRequest request) {


        if (request.getHeader("Authorization") != null) {
            String username = jwtTokenUtil.getUsernameFromToken(request.getHeader("Authorization").substring(7));

            User current = userService.findByEmail(username);

            if (current.getRoles().contains("ADMIN")) {

                return new ResponseEntity<>("{\"role\":\"ADMIN\"}", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("{\"role\":\"OTHERS\"}", HttpStatus.OK);

    }

    @GetMapping("/getusername")
    public String getUserName(HttpServletRequest request) {

        if (request.getHeader("Authorization") != null) {
            String username = jwtTokenUtil.getUsernameFromToken(request.getHeader("Authorization").substring(7));
            return username;
        } else {
            return "INVALID JWT";
        }

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
