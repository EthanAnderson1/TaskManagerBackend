package com.example.TaskManagerBackend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.TaskManagerBackend.models.User;
import com.example.TaskManagerBackend.repository.UserRepository;

@Service
public class UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    public User addUser(User user){
        System.out.println("Adding user: " + user.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void removeUser(String username) {
         System.out.println("Removing user: " + username);
       User user = userRepository.findByUsername(username);
       if(user == null){
        throw new UsernameNotFoundException("User not found");
       }
       userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        System.out.println("Fetching all users");
        return userRepository.findAll();
    }

    public User getUser(String username) {
        System.out.println("Fetching user: " + username);
        return userRepository.findByUsername(username);
    }

	public String verify(User user) {
        System.out.println("Verifying user: " + user.getUsername());
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		if(authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());
        }
        return "fail";
	}

}
