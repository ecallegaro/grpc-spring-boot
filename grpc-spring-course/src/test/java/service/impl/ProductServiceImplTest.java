package service.impl;

import br.com.content4devs.domain.Product;
import br.com.content4devs.dto.ProductInputDTO;
import br.com.content4devs.exception.ProductAlreadyExistsException;
import br.com.content4devs.exception.ProductNotFoundException;
import br.com.content4devs.repository.ProductRepository;
import br.com.content4devs.service.impl.ProductServiceImpl;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    @DisplayName("when create product service is call with valid data a product is returned")
    public void createProductSuccessTest() {
        Product product = new Product(1L, "product name", 10.00, 10);

        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        var productInputDto = new ProductInputDTO("product name", 10.00, 10);
        var productOutput = service.create(productInputDto);

        Assertions.assertThat(productOutput)
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    @DisplayName("when create product service is call with duplicated name, throw ProductAlreadyExistsException ")
    public void createProductExceptionTest() {
        Product product = new Product(1L, "product name", 10.00, 10);

        Mockito.when(repository.findByNameIgnoreCase(Mockito.any())).thenReturn(Optional.of(product));

        var productInputDto = new ProductInputDTO("product name", 10.00, 10);

        Assertions.assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> service.create(productInputDto));
    }

    @Test
    @DisplayName("when findById is called with valid id a product is returned")
    public void findByIdSuccessTest() {
        Long id = Long.valueOf(1);

        Product product = new Product(1L, "product name", 10.00, 10);

        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(product));

        var productOutput = service.findById(id);

        Assertions.assertThat(productOutput)
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    @DisplayName("when delete product is call with id should does not throw ")
    public void deleteSuccessTest() {
        Long id = Long.valueOf(1);

        Product product = new Product(1L, "product name", 10.00, 10);

        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(product));

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(id));
    }

    @Test
    @DisplayName("when delete product is call with invalid id throws ProductNotFoundException")
    public void deleteExceptionTest() {
        Long id = Long.valueOf(1);

        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(ProductNotFoundException.class).isThrownBy(() -> service.delete(id));
    }

    @Test
    @DisplayName("when findById is called with invalid id throws ProductNotFoundException")
    public void findByIdExceptionTest() {
        Long id = Long.valueOf(1);

        Product product = new Product(1L, "product name", 10.00, 10);

        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> service.findById(id));
    }

    @Test
    @DisplayName("when findAll is call a list of product is returned")
    public void findByAllSuccessTest() {

        var products = List.of(
                new Product(1L, "product one", 10.00, 10),
                new Product(2L, "product two", 10.00, 100));

        Mockito.when(repository.findAll()).thenReturn(products);

        var productOutput = service.findAll();

        Assertions.assertThat(productOutput)
                .extracting("id", "name", "price", "quantityInStock")
                .contains(
                        Tuple.tuple(1L, "product one", 10.00, 10),
                        Tuple.tuple(2L, "product two", 10.00, 100)
                );
    }
}
