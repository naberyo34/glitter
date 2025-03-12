package com.example.demo.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.generated.User;

public class UserDetailsImpl implements UserDetails {
  private final String id;
  private final String password;
  private final Collection <? extends GrantedAuthority> authorities;

  public UserDetailsImpl(User user) {
    this.id = user.getId();
    this.password = user.getPassword();
    this.authorities = Arrays.stream(user.getRole().split(",")).map((role) -> new SimpleGrantedAuthority(role)).toList();
  }

  @Override
  public Collection <? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    // これはログインに使う値のため id で正しい
    return id;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
