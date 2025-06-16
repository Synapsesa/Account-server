package com.synapse.account_service.convert.authority;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomAuthorityMapperTest {
    private final CustomAuthorityMapper authorityMapper = new CustomAuthorityMapper();

    @Test
    @DisplayName("권한 매핑 성공: 'ROLE_' 접두사가 없는 권한에 접두사를 추가한다")
    void mapAuthorities_addPrefix() {
        // given
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("USER"));

        // when
        Collection<? extends GrantedAuthority> mappedAuthorities = authorityMapper.mapAuthorities(authorities);

        // then
        assertThat(mappedAuthorities).hasSize(1);
        assertThat(mappedAuthorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("권한 매핑 성공: 'ROLE_' 접두사가 이미 있는 권한은 그대로 유지한다")
    void mapAuthorities_keepExistingPrefix() {
        // given
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // when
        Collection<? extends GrantedAuthority> mappedAuthorities = authorityMapper.mapAuthorities(authorities);

        // then
        assertThat(mappedAuthorities).hasSize(1);
        assertThat(mappedAuthorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("권한 매핑 성공: 빈 권한 목록은 빈 목록으로 반환한다")
    void mapAuthorities_emptyList() {
        // given
        Set<GrantedAuthority> authorities = Collections.emptySet();

        // when
        Collection<? extends GrantedAuthority> mappedAuthorities = authorityMapper.mapAuthorities(authorities);

        // then
        assertThat(mappedAuthorities).isEmpty();
    }
}
