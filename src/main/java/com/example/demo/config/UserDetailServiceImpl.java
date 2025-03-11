package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  
  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    User _user = userRepository.findById(id);

    if (_user == null) {
      throw new UsernameNotFoundException("User not found for id: " + id);
    }

    return new UserDetailsImpl(_user);
  }
}
