package nablarch.common.authorization.action;

import nablarch.common.authorization.CheckAuthority;

public class FooService {

    @CheckAuthority("FOO")
    public void publicMethod() {}
}
