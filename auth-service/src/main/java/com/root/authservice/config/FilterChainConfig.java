package com.root.authservice.config;

import com.root.authservice.utils.JwtHandler;
import com.root.crossdbservice.entities.RoleEntity;
import com.root.crossdbservice.entities.UserEntity;
import com.root.crossdbservice.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class FilterChainConfig /*extends OncePerRequestFilter*/ {
   /* private final UserRepository userRepository;
    private final JwtHandler jwtHandler;

    public FilterChainConfig(UserRepository userRepository, JwtHandler jwtHandler) {
        this.userRepository = userRepository;
        this.jwtHandler = jwtHandler;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String getToken = this.retrieveToken(request);

        if (getToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Auth token not found");
        }

        String parseTokenSubject = this.jwtHandler.verifyToken(getToken);

        UserEntity getUser = this.userRepository.findById(
                UUID.fromString(parseTokenSubject)
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User non authorized"));

        Set<RoleEntity> roles = new HashSet<>();

        getUser.getUserRoles().forEach(userRole -> {
            roles.add(new RoleEntity(userRole.getRole().getRole()));
        });

        UserDetails user = new UserDetailsImpl(
                getUser.getId().toString(),
                getUser.getPassword(),
                roles
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String retrieveToken(HttpServletRequest request){
        String authHeader = request.getHeader("Bearer");
        if(authHeader == null) return null;

        return authHeader.replace("Bearer ", "");
    }*/
}