package dev.simoncodes.ledger.user;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class UserDetailsAdapter implements UserDetails {

    private final User user;

    public UserDetailsAdapter(User u) {
        this.user = u;
    }

    @NonNull
    @Override
    public String getUsername() {
        return this.user.getId().toString();
    }

    @Override
    public String getPassword() {
        return this.user.getPasswordHash();
    }

    @NonNull
    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired(){
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
