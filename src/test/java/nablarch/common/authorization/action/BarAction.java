package nablarch.common.authorization.action;

import nablarch.common.authorization.CheckAuthority;

public class BarAction {

    @CheckAuthority("FOO")
    public void publicMethod() {}
}
