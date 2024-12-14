package hexlet.code.service;

import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CustomUserDetailsService implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Override
    public void createUser(UserDetails user) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public boolean userExists(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

}
