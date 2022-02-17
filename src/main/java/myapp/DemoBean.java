package myapp;


import io.micronaut.runtime.http.scope.RequestScope;

@RequestScope
public class DemoBean {
  public DemoBean() {}

  public int getBeanIdentity() {
    return System.identityHashCode(this);
  }
}