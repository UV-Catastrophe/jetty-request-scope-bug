package myapp;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.security.annotation.Secured;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Secured({"USER"})
@Controller("/api/foo-service")
public class FooController {

  private static final Logger log = LoggerFactory.getLogger(FooController.class);

  private final FooService fooService = new FooService();
  private final DemoBean demoBean;

  public FooController(final DemoBean demoBean) {
    this.demoBean = demoBean;
  }

  @Post(uri = "/bars", produces = APPLICATION_JSON)
  public HttpResponse<BarDto> create(@Body final String id) {
    return HttpResponse.created(fooService.create(id));
  }

  @Put(uri = "/bars/{id}", produces = APPLICATION_JSON)
  public HttpResponse<BarDto> update(@PathVariable final String id) {
    return HttpResponse.created(fooService.create(id));
  }

  @Get(uri = "/bars/{id}", produces = APPLICATION_JSON)
  public HttpResponse<BarDto> findById(@PathVariable final String id) {
    return fooService.findById(id)
        .map(HttpResponse::ok)
        .orElseGet(HttpResponse::notFound);
  }

  @Get(uri = "/bars", produces = APPLICATION_JSON)
  public HttpResponse<List<BarDto>> findAll(
      @QueryValue("id_fragment") final Optional<String> idFragment) {
    final List<BarDto> filteredResult = fooService.findAll().stream()
        .filter(bar -> filterByIdFragment(bar, idFragment))
        .collect(Collectors.toList());
    return HttpResponse.ok(filteredResult);
  }

  @Delete(uri = "/bars/{id}", produces = APPLICATION_JSON)
  public HttpResponse<?> remove(@PathVariable final String id) {
    fooService.remove(id);
    return HttpResponse.accepted();
  }

  @Get("/test")
  public HttpResponse<?> test() {
    // Request body present
    log.info("Check ServerRequestContext in FooController: {}",
        ServerRequestContext.currentRequest());
    // Prints value
    log.info("Check RequestScope Bean in FooController: {}",
        demoBean.getBeanIdentity());

    return HttpResponse.noContent();
  }


  private boolean filterByIdFragment(final BarDto barDto,
      final Optional<String> idFragmentOptional) {
    return idFragmentOptional.stream()
        .allMatch(idFragment -> barDto.getId().toLowerCase().contains(idFragment.toLowerCase()));
  }
}
