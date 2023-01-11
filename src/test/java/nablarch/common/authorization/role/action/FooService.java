package nablarch.common.authorization.role.action;

import nablarch.common.authorization.role.CheckRole;

public class FooService {

    @CheckRole("FOO")
    public void publicMethod() {}
}
