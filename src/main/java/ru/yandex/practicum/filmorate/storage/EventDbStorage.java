package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event addEvent(Event event) {
        String sqlQuery = "INSERT INTO events " +
                "(timestamp, " +
                "user_id, " +
                "event_type, " +
                "operation, " +
                "entity_id) " +
                "VALUES (?, " +
                "?, " +
                "(SELECT id FROM event_types WHERE event_type_name = ?), " +
                "(SELECT id FROM operations WHERE operation_name = ?), " +
                "?)";

        KeyHolder holder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatement = con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, new String[]{"event_id"});
            ps.setLong(1, event.getTimestamp());
            ps.setLong(2, event.getUserId());
            ps.setString(3, event.getEventType());
            ps.setString(4, event.getOperation());
            ps.setLong(5, event.getEntityId());
            return ps;
        };
        jdbcTemplate.update(preparedStatement, holder);
        long eventId = Objects.requireNonNull(holder.getKey()).longValue();
        if (eventId == 0) {
            return null;
        }
        return event.withEventId(eventId);
    }

    @Override
    public List<Event> getUserEvents(Long id) {
        String sqlQuery = "SELECT e.timestamp, " +
                "e.user_id, " +
                "et.event_type_name, " +
                "o.operation_name, " +
                "e.event_id, " +
                "e.entity_id " +
                "FROM events e " +
                "LEFT JOIN event_types et ON e.event_type = et.id " +
                "LEFT JOIN operations o ON e.operation = o.id " +
                "WHERE e.user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, id);
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .timestamp(resultSet.getLong("timestamp"))
                .userId(resultSet.getLong("user_id"))
                .eventType(resultSet.getString("event_type_name"))
                .operation(resultSet.getString("operation_name"))
                .eventId(resultSet.getLong("event_id"))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }
}
