package lab.org.api.composite.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SecurityRequirement(name = "security_auth")
@Tag(name = "CompositeService", description = "REST API for composite product information.")
@RequestMapping("/api/product")
public interface ProductCompositeService {

    @Operation(
            summary = "${api.product-composite.get-composite-product.description}",
            description = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @GetMapping(
            value = "/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Mono<ProductAggregate> getProduct(
            @RequestHeader HttpHeaders headers,
            @PathVariable int productId,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent);

    @Operation(
            summary = "${api.product-composite.list-composite-product.description}",
            description = "${api.product-composite.list-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
    })
    @GetMapping(
            value = "/list/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Flux<ProductAggregate> listProduct();

    @Operation(
            summary = "${api.product-composite.create-composite-product.description}",
            description = "${api.product-composite.create-composite-product.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(
            value = "",
            consumes = "application/json")
    Mono<Void> createProduct(@RequestBody ProductAggregate body);

    @Operation(
            summary = "${api.product-composite.delete-composite-product.description}",
            description = "${api.product-composite.delete-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = "/{productId}")
    Mono<Void> deleteProduct(@PathVariable int productId);
}
