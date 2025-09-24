package org.yenln8.ChatApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "native_languages")
public class NativeLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    @Enumerated(EnumType.STRING)
    private CODE code;

    @OneToMany(mappedBy = "nativeLanguage", fetch = FetchType.EAGER)
    private List<NativeLanguageLocale> nativeLanguageLocales;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder.Default
    @Column(name = "deleted", columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    @JsonIgnore
    private Long deleted = 0L;

    @Version
    private Integer rowVersion;

    public enum CODE {
        EN,
        VN
    }
}