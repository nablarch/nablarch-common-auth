package nablarch.common.authorization.role.action;

import nablarch.common.authorization.role.CheckAuthority;

public class BarAction {

    @CheckAuthority("FOO")
    public void publicMethod() {}
}
