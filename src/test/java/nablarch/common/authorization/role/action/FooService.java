package nablarch.common.authorization.role.action;

import nablarch.common.authorization.role.CheckAuthority;

public class FooService {

    @CheckAuthority("FOO")
    public void publicMethod() {}
}
