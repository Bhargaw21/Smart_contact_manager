package com.scm.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    private String userId;

    @Column(name = "user_Name", nullable = false)
    private String Name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String about;

    private String password; // ✅ Don't suppress the getter!

    @Column(columnDefinition = "TEXT")
    private String profilepic;

    private String phoneNumber;

    private boolean enabled = false;
    private boolean emailVerified = false;
    private boolean phoneNummberverified = false;

    @Enumerated(value = EnumType.STRING)
    private Providers provider = Providers.SELF;

    private String providerUserId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<contact> contacts = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> rolelist = new ArrayList<>();

    private String emailToken;

    // ===== Spring Security UserDetails methods =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolelist.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
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
        return this.enabled;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return email;
    }

    public String getAbout() {
        return about;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneNummberverified() {
        return phoneNummberverified;
    }

    public Providers getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public List<contact> getContacts() {
        return contacts;
    }

    public List<String> getRolelist() {
        return rolelist;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setPhoneNummberverified(boolean phoneNummberverified) {
        this.phoneNummberverified = phoneNummberverified;
    }

    public void setProvider(Providers provider) {
        this.provider = provider;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public void setContacts(List<contact> contacts) {
        this.contacts = contacts;
    }

    public void setRolelist(List<String> rolelist) {
        this.rolelist = rolelist;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }
}
