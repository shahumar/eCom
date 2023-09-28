package lab.org.api.composite.category;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@SecurityRequirement(name = "security_auth")
@Tag(name = "CategoryCompositeService", description = "REST API for composite category information.")
@RequestMapping("/api/category")
public interface CategoryCompositeService {


    @Operation(summary = "${api.category.get-composite-category.description}",
            description = "${api.category.get-composite-category.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @GetMapping(value = "/{categoryId}", produces = APPLICATION_JSON_VALUE)
    Mono<CategoryAggregate> getCategory(@RequestHeader HttpHeaders headers, @PathVariable int categoryId);

    @Operation(
            summary = "${api.category.create-composite-category.description}",
            description = "${api.category.create-composite-category.notes}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "", consumes = "application/json")
    Mono<Void> createCategory(@RequestBody CategoryAggregate body);


    @Operation(
            summary = "${api.category.delete-composite-category.description}",
            description = "${api.category.delete-composite-category.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{categoryId}")
    Mono<Void> deleteCategory(@PathVariable("categoryId") int categoryId);

    @Operation(summary = "${api.category.list-composite-category.description}",
            description = "${api.category.list-composite-category.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
    })
    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    Flux<CategoryAggregate> listCategories();


}
