package org.yenln8.ChatApp.entity;

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
@Builder(toBuilder = true)
@Entity
@Table(name = "question_tests")

public class QuestionTests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false)
    private Long orderNumber;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "explains", nullable = true)
    private String explain;

    @OneToMany(mappedBy = "questionTest", fetch = FetchType.LAZY)
    private List<QuestionOptions> questionOptions;

    @ManyToOne(fetch = FetchType.LAZY)
    private Test test;

    @OneToMany(mappedBy = "questionTest", fetch = FetchType.LAZY)
    private List<QuestionHistory> questionHistories;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}