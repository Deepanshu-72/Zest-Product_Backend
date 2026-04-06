package com.SubString.products.products_app.security;

import com.SubString.products.products_app.entity.User;
import com.SubString.products.products_app.exceptions.ResourceNotFoundException;
import com.SubString.products.products_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private  final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(()-> new ResourceNotFoundException("Invalid EmailId"));
        return user;
    }
}
