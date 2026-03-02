package com.rev.app.repository;

import com.rev.app.entity.Connection;
import com.rev.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ConnectionRepositoryTest {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User("user1", "user1@example.com", "pass");
        user2 = new User("user2", "user2@example.com", "pass");
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void testAreConnected() {
        Connection con = new Connection();
        con.setSender(user1);
        con.setReceiver(user2);
        con.setStatus(Connection.Status.ACCEPTED);
        connectionRepository.save(con);

        assertThat(connectionRepository.areConnected(user1.getId(), user2.getId())).isTrue();
        assertThat(connectionRepository.areConnected(user2.getId(), user1.getId())).isTrue();
    }

    @Test
    public void testFindAcceptedConnections() {
        Connection con = new Connection();
        con.setSender(user1);
        con.setReceiver(user2);
        con.setStatus(Connection.Status.ACCEPTED);
        connectionRepository.save(con);

        List<Connection> connections = connectionRepository.findAcceptedConnections(user1.getId());
        assertThat(connections).hasSize(1);
    }

    @Test
    public void testFindConnectionBetween() {
        Connection con = new Connection();
        con.setSender(user1);
        con.setReceiver(user2);
        connectionRepository.save(con);

        Optional<Connection> result = connectionRepository.findConnectionBetween(user1.getId(), user2.getId());
        assertThat(result).isPresent();
    }
}
