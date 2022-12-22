package resources;

import br.com.content4devs.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/*@SpringBootTest(properties = {
        "grpc.server.inProcessName=test", // Enable inProcess server
        "grpc.server.port=-1", // Disable external server
        "grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@SpringJUnitConfig(classes = {ProductResourceIntegrationTest.class})
@DirtiesContext

@Configuration
@ImportAutoConfiguration({
        GrpcServerAutoConfiguration.class, // Create required server beans
        GrpcServerFactoryAutoConfiguration.class, // Select server implementation
        GrpcClientAutoConfiguration.class}) // Support @GrpcClient annotation
@TestPropertySource("classpath:application-test.properties")
@SpringBootApplication*/
public class ProductResourceIntegrationTest {

    @GrpcClient("inProcess")
    private ProductServiceGrpc.ProductServiceBlockingStub serviceBlockingStub;

    @Autowired
    Flyway flyway;


    @DisplayName("When valid data is provided a product is created")

    @Test
    @DirtiesContext
    public void createProductSuccessTest() {
        var request = ProductRequest.newBuilder()
                .setName("Product name")
                .setPrice(10.00)
                .setQuantityInStock(100)
                .build();

        var response = serviceBlockingStub.create(request);

        Assertions.assertThat(request)
                .usingRecursiveComparison()
                .comparingOnlyFields("name", "price", "quantity_in_stock")
                .isEqualTo(response);

    }

    @Test
    @DirtiesContext
    @DisplayName("when create is called with duplicated name, throw ProductAlreadyExistsException")
    public void createProductAlreadyExistsExceptionTest() {
        ProductRequest request = ProductRequest.newBuilder()
                .setName("Product A")
                .setPrice(10.00)
                .setQuantityInStock(100)
                .build();

        ProductResponse response = serviceBlockingStub.create(request);

        Assertions.assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> serviceBlockingStub.create(request))
                .withMessage("ALREADY_EXISTS: Produto A já cadastraodo no sistema. ");

    }

    @Test
    @DisplayName("when findById is call with invalid throw ProductNotFoundException")
    public void findByIdExceptionTest() {
        RequestById request = RequestById.newBuilder().setId(100L).build();

        Assertions.assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> serviceBlockingStub.findById(request))
                .withMessage("NOT_FOUND : Produto com ID 100 não encontrado");

    }

    @Test
    @DisplayName("when delete is call with id should does not throw")
    public void deleteSuccessTest() {
        RequestById request = RequestById.newBuilder().setId(1L).build();

        Assertions.assertThatNoException().isThrownBy(() -> serviceBlockingStub.delete(request));

    }

    @Test
    @DisplayName("when delete is call with id should throw ProductNotFoundException")
    public void deleteExceptionTest() {
        RequestById request = RequestById.newBuilder().setId(100L).build();

        Assertions.assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> serviceBlockingStub.delete(request))
                .withMessage("NOT_FOUND : Produto com ID 100 não encontrado");

    }

    @Test
    @DisplayName("when findAll method is call a product list is returned")
    public void findAllSuccessTest() {
        var response = serviceBlockingStub.findAll(EmptyRequest.newBuilder().build());

        Assertions.assertThat(response).isInstanceOf(ProductResponseList.class);
        Assertions.assertThat(response.getProductsCount()).isEqualTo(2);
        Assertions.assertThat(response.getProductsList())
                .extracting("id", "name", "price", "quantityInStock")
                .contains(
                        Tuple.tuple(1L, "Product A", 10.99, 10),
                        Tuple.tuple(2L, "Product B", 10.99, 10)
                );
    }

    @BeforeEach
    public void setUp() {
        flyway.clean();
        flyway.migrate();
    }
}
