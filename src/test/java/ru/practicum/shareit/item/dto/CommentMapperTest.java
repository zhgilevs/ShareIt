package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;

class CommentMapperTest {

    Comment comment;
    CommentDto commentDto;

    @BeforeEach
    void init() {
        LocalDateTime now = LocalDateTime.now();
        comment = new Comment(1L, "Хорошая дрель",
                new Item(1L, "Дрель", "Простая дрель", true,
                        new User(1L, "owner", "owner@ya.ru"), null),
                new User(2L, "author", "author@ya.ru"), now);
        commentDto = new CommentDto(1L, "Хорошая дрель", "author", now);
    }

    @Test
    void test_toComment() {
        Comment returnedComment = toComment(commentDto);
        assertEquals(comment.getText(), returnedComment.getText());
    }

    @Test
    void test_toCommentDto() {
        CommentDto returnedCommentDto = toCommentDto(comment);
        assertEquals(commentDto.getId(), returnedCommentDto.getId());
        assertEquals(commentDto.getText(), returnedCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), returnedCommentDto.getAuthorName());
        assertEquals(commentDto.getCreated(), returnedCommentDto.getCreated());
    }
}