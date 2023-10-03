package ru.practicum.shareit.item;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "comment_text", nullable = false)
    String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_item_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    User author;

    @Column(name = "comment_created", nullable = false)
    LocalDateTime created;
}
