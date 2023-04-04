package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
            PreparedStatement ps = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, event.getTimestamp());
            ps.setLong(2, event.getUserId());
            ps.setString(3, event.getEventType().toString());
            ps.setString(4, event.getOperation().toString());
            ps.setLong(5, event.getEntityId());
            return ps;
        };
        jdbcTemplate.update(preparedStatement, holder);
        long eventId = holder.getKey().longValue();
        event.setEventId(eventId);
        return event;
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
                .eventType(EventType.valueOf(resultSet.getString("event_type_name")))
                .operation(Operations.valueOf(resultSet.getString("operation_name")))
                .eventId(resultSet.getLong("event_id"))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }
}
