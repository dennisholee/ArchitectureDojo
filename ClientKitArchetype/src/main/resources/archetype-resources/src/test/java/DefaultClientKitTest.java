package ${package};

import ${package}.config.ClientConfiguration;
import ${package}.service.PetStoreService;
import ${package}.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class DefaultClientKitTest {

    private ClientKit<PetStoreService> clientKit;

    @BeforeEach
    void setUp() {
        ClientConfiguration config = TestUtils.createTestConfig();
        clientKit = new DefaultClientKit<>(config, PetStoreService.class);
    }

    @Test
    void initialization_WithValidConfig_ShouldSucceed() {
        assertTrue(clientKit.isInitialized());
        assertNotNull(clientKit.getService());
    }

    @Test
    void getService_ShouldReturnSameInstance() {
        PetStoreService service1 = clientKit.getService();
        PetStoreService service2 = clientKit.getService();

        assertNotNull(service1);
        assertSame(service1, service2, "Service instance should be singleton");
    }

    @Test
    void close_ShouldCleanupResources() throws Exception {
        clientKit.close();
        assertFalse(clientKit.isInitialized());
    }

    @Test
    void constructor_WithNullConfig_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            new DefaultClientKit<>(null, PetStoreService.class)
        );
    }

    @Test
    void constructor_WithNullServiceClass_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            new DefaultClientKit<>(TestUtils.createTestConfig(), null)
        );
    }
}
