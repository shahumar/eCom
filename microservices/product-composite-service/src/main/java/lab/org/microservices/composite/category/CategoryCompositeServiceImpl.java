package lab.org.microservices.composite.category;

import lab.org.api.composite.category.CategoryAggregate;
import lab.org.api.composite.category.CategoryCompositeService;
import lab.org.api.core.product.Category;
import lab.org.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@RestController
public class CategoryCompositeServiceImpl implements CategoryCompositeService {

	private static final Logger LOG = LoggerFactory.getLogger(CategoryCompositeServiceImpl.class);

	private CategoryCompositeIntegration integration;
	private final ServiceUtil serviceUtil;
    private SecurityContext nullSecCtx = new SecurityContextImpl();


    @Autowired
    public CategoryCompositeServiceImpl(CategoryCompositeIntegration integration, ServiceUtil serviceUtil) {
        this.integration = integration;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<CategoryAggregate> getCategory(HttpHeaders headers, int categoryId) {
        LOG.info("Will get category by categoryID: {}", categoryId);

        return Mono.zip(
                values -> createAggregateCategory(
                        (SecurityContext) values[0],
                        (Category) values[1]),
                    getLogAuthorizationInfoMono(),
                    integration.getCategory(headers, categoryId))
                .doOnError(ex -> LOG.warn("getCategory failed {}", ex.toString()))
                .log(LOG.getName(), Level.FINE);

    }

    @Override
    public Flux<CategoryAggregate> listCategories() {
        LOG.info("List all the categories");
        var categories = integration.listCategories();
        if (categories == null) {
            return Flux.fromIterable(List.of());
        }
        var securityContextMono = getSecurityContextMono();
        return categories.flatMap(cat -> securityContextMono.map(
                sc -> createAggregateCategory((SecurityContext) sc, cat)));

    }

    private CategoryAggregate createAggregateCategory(SecurityContext sc, Category category) {
        logAuthorizationInfo(sc);
        int categoryId = category.getCategoryId();
        String name = category.getName();
        LOG.info("{} : {} : {}", categoryId, name, category.getSlug());
        return new CategoryAggregate(categoryId, name, null, category.getSlug());
    }

    @Override
    public Mono<Void> createCategory(CategoryAggregate body) {
        try {
            List<Mono> monoList = new ArrayList<>();
            monoList.add(getLogAuthorizationInfoMono());
            LOG.debug("create Category");
            Category category = new Category(body.getCategoryId(), body.getName(), null, body.getParentId());
            monoList.add(integration.createCategory(category));
            return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
                    .doOnError(ex -> LOG.warn("Category failed: {}", ex.toString()))
                    .then();
        } catch (RuntimeException e) {
            LOG.warn("Category failed", e);
            throw e;
        }
    }

    @Override
    public Mono<Void> deleteCategory(int categoryId) {
        try {
            LOG.debug("Delete a Category with ID: ", categoryId);
            return Mono.zip(r -> "",
                    getLogAuthorizationInfoMono(),
                    integration.deleteCategory(categoryId))
                    .doOnError(ex -> LOG.error("Delete failed for ID {}", ex.getMessage()))
                    .log(LOG.getName(), Level.FINE)
                    .then();
        } catch (RuntimeException e) {
            LOG.error("Error {}", e);
            throw e;
        }
    }

    private Mono getLogAuthorizationInfoMono() {
        return getSecurityContextMono().doOnNext(sc -> logAuthorizationInfo((SecurityContext) sc));
    }

    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwt);
        } else {
            LOG.warn("No JWT based authentication supplied, running tests are we?");
        }
    }

    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No JWT supplied running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object exp = jwt.getClaims().get("exp");
                LOG.debug("Authorization inof: Subject: {}, Scopes: {}, Expire: {}, Issuer: {}, Audience: {}", subject, scopes, exp, issuer, audience);
            }
        }
    }

    private Mono getSecurityContextMono() {
        return ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSecCtx);
    }
}
