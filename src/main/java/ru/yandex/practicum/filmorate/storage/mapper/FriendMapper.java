package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс маппера для преобразования строк базы данных в объекты типа {@link Friend}.
 */

public class FriendMapper implements RowMapper<Friend> {

    /**
     * Преобразует строку результата SQL-запроса в объект типа {@link Friend}.
     *
     * @param rs     результирующее множество записей
     * @param rowNum номер строки
     * @return объект типа {@link Friend}, соответствующий строке результата
     * @throws SQLException если возникает ошибка при извлечении данных из ResultSet
     */
    @Override
    public Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friend friend = new Friend();
        friend.setUserId(rs.getLong("user_id"));
        friend.setFriendId(rs.getLong("friend_id"));
        friend.setStatus(rs.getBoolean("status"));
        return friend;
    }
}