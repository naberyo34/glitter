package com.example.demo.domain.Auth;

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
    // TODO: user 以外の権限が必要になったら再考
    this.authorities = Arrays.asList(new SimpleGrantedAuthority("user"));
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
