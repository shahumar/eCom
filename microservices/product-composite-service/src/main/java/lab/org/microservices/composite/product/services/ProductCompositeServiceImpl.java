package lab.org.microservices.composite.product.services;

import lab.org.api.composite.product.*;
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
import lab.org.util.http.ServiceUtil;
import lab.org.api.core.product.Product;
import lab.org.api.core.review.Review;
import lab.org.api.core.recommendation.Recommendation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;


@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private ProductCompositeIntegration integration;
    private final SecurityContext nullSecCtx = new SecurityContextImpl();


    @Autowired
    public ProductCompositeServiceImpl(
            ServiceUtil serviceUtil, ProductCompositeIntegration integration) {

        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<ProductAggregate> getProduct(HttpHeaders headers, int productId, int delay, int faultPercent) {
        LOG.info("Will get composite product info for product.id={}", productId);
        return Mono.zip(
                values -> createProductAggregate(
                        (SecurityContext) values[0],
                        (Product) values[1],
                        (List<Recommendation>) values[2],
                        (List<Review>) values[3],
                        serviceUtil.getServiceAddress()),
                getSecurityContextMono(),
                integration.getProduct(headers, productId, delay, faultPercent),
                integration.getRecommendations(headers, productId).collectList(),
                integration.getReviews(headers, productId).collectList())
                .doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
                .log(LOG.getName(), Level.FINE);
    }

    @Override
    public Flux<ProductAggregate> listProduct() {
        LOG.info("will cate all the products");
        Flux<Product> products = integration.listProducts();
        if (products == null) {
            return Flux.fromIterable(List.of());
        }
        return transform(products);

    }



    @Override
    public Mono<Void> createProduct(ProductAggregate body) {
        try {

            List<Mono> monoList = new ArrayList<>();
            monoList.add(getLogAuthorizationInfoMono());
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getProductId());
            Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
            monoList.add(integration.createProduct(product));

            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(r -> {
                    Recommendation recommendation = new Recommendation(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
                    monoList.add(integration.createRecommendation(recommendation));
                });
            }
            if (body.getReviews() != null) {
                body.getReviews().forEach(r -> {
                    Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
                    monoList.add(integration.createReview(review));
                });
            }

            LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.getProductId());
            return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
                    .doOnError(ex -> LOG.warn("createCompositeProduct failed: {}", ex.toString()))
                    .then();
        } catch (RuntimeException e) {
            LOG.warn("createCompositeProduct failed", e);
            throw e;
        }
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {

        try {
            LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
            return Mono.zip(r -> "",
                    getLogAuthorizationInfoMono(),
                    integration.deleteProduct(productId),
                    integration.deleteReviews(productId),
                    integration.deleteRecommendations(productId))
                    .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
                    .log(LOG.getName(), Level.FINE).then();
        } catch (RuntimeException re) {
            LOG.warn("deleteCompositeProduct failed: {}", re.toString());
            throw re;
        }
    }

    private ProductAggregate createProductAggregate(
            SecurityContext sc,
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress) {

        logAuthorizationInfo(sc);
        int productId = product.getProductId();
        String name = product.getName();
        BigDecimal weight = product.getWeight();

        List<RecommendationSummary> recommendationSummaries =
                (recommendations == null) ? null : recommendations.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                        .collect(Collectors.toList());

        List<ReviewSummary> reviewSummaries =
                (reviews == null) ? null : reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }

    private Mono<SecurityContext> getLogAuthorizationInfoMono() {
        return getSecurityContextMono().doOnNext(sc -> logAuthorizationInfo((SecurityContext) sc));

    }

    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken) sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No JWT based authentication supplied, running tests are we?");
        }
    }

    private void logAuthorizationInfo(Jwt token) {
        if (token == null) {
            LOG.warn("No JWT supplied running tests are we?");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = token.getIssuer();
                List<String> audience = token.getAudience();
                Object subject = token.getClaims().get("sub");
                Object scopes = token.getClaims().get("scope");
                Object expires = token.getClaims().get("exp");
                LOG.debug("Authorization inof: Subject: {}, Scopes: {}, Expire: {}, Issuer: {}, Audience: {}", subject, scopes, expires, issuer, audience);
            }
        }

    }

    private Mono getSecurityContextMono() {
        return ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSecCtx);
    }

    private Flux<ProductAggregate> transform(Flux<Product> sourceFlux) {
        return sourceFlux.flatMap(this::transformToTarget);
    }

    private Mono<ProductAggregate> transformToTarget(Product source) {
        var mono = getSecurityContextMono();
        return mono.map(sc -> createProductAggregate(
                (SecurityContext) sc,
                source,
                List.of(),
                List.of(),
                serviceUtil.getServiceAddress()));
    }

}
