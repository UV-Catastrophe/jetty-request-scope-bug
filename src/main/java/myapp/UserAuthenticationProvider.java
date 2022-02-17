package myapp;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Set;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class UserAuthenticationProvider implements AuthenticationProvider {

  private static final Logger log = LoggerFactory.getLogger(UserAuthenticationProvider.class);

  private final DemoBean demoBean;

  public UserAuthenticationProvider(final DemoBean demoBean) {
    this.demoBean = demoBean;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(
      @Nullable final HttpRequest<?> httpRequest,
      final AuthenticationRequest<?, ?> authenticationRequest) {
    return Flowable.create(
        emitter -> {
          if (httpRequest == null) {
            emitter.onError(
                new AuthenticationException(
                    new AuthenticationFailed("HttpRequest expected but not available")));
          } else {
            // Returns empty
            log.info("Check ServerRequestContext in AuthenticationProvider: {}",
                ServerRequestContext.currentRequest());
            // Throws exception
            log.info("Check RequestScope Bean in AuthenticationProvider: {}",
                demoBean.getBeanIdentity());

            emitter.onNext(
                authenticationToResponse(
                    Authentication.build("dummy@gmail.com", Set.of("USER"), Map.of())));
          }
        },
        BackpressureStrategy.ERROR);
  }

  public static AuthenticationResponse authenticationToResponse(
      final Authentication authentication) {
    return AuthenticationResponse.success(
        authentication.getName(), authentication.getRoles(), authentication.getAttributes());
  }
}
