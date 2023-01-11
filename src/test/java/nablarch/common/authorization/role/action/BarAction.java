package nablarch.common.authorization.role.action;

import nablarch.common.authorization.role.CheckRole;

public class BarAction {

    @CheckRole("FOO")
    public void publicMethod() {}
}
