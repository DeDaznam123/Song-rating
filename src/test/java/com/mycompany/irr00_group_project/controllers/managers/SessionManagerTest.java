// package com.mycompany.irr00_group_project.controllers.managers;

// import static org.junit.jupiter.api.Assertions.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import com.mycompany.irr00_group_project.models.User;
// import com.mycompany.irr00_group_project.services.Database;

// /**
//  * Unit tests for the SessionManager singleton.
//  */
// public class SessionManagerTest {
//     private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files/";
//     private Database db;

//     /** Reset the session before each test runs. */
//     @BeforeEach
//     void setUp() {
//         SessionManager.getInstance().clearSession();
//         db = new Database(TEST_DB_PATH);
//     }

//     /** getInstance() must always return the same object (singleton). */
//     @Test
//     void testSingletonInstance() {
//         SessionManager s1 = SessionManager.getInstance();
//         SessionManager s2 = SessionManager.getInstance();
//         assertSame(s1, s2, "getInstance() should always return the same instance");
//     }

//     /** After setCurrentUser(), getCurrentUser() should return that user. */
//     @Test
//     void testSetAndGetCurrentUser() {
//         User u = new User("1", "testuser", "test@example.com");
//         SessionManager.getInstance().setCurrentUser(u);

//         assertSame(u,
//                 SessionManager.getInstance().getCurrentUser(),
//                 "CurrentUser should be the one that was set");
//     }

//     /** clearSession() must remove the stored user. */
//     @Test
//     void testClearSession() {
//         User u = new User("1", "testuser", "test@example.com");
//         SessionManager.getInstance().setCurrentUser(u);
//         SessionManager.getInstance().clearSession();

//         assertNull(SessionManager.getInstance().getCurrentUser(),
//                 "CurrentUser should be null after clearSession()");
//     }
// }
