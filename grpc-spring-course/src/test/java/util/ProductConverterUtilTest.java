package util;

import br.com.content4devs.domain.Product;
import br.com.content4devs.dto.ProductInputDTO;
import br.com.content4devs.util.ProductConverterUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductConverterUtilTest {

    @Test
    public void productToProductOutputDtoTest() {
        var product = new Product(1L, "product name", 10.00, 10);
        var productOutputDto = ProductConverterUtil.productToProductOutputDto(product);
        Assertions.assertThat(product)
                .usingRecursiveComparison().isEqualTo(productOutputDto);
    }

    @Test
    public void productInputToProductTest() {
        var productInput = new ProductInputDTO("product name", 10.00, 10);
        var product = ProductConverterUtil.productInputDtoToProduct(productInput);
        Assertions.assertThat(productInput)
                .usingRecursiveComparison().isEqualTo(product);
    }
}
