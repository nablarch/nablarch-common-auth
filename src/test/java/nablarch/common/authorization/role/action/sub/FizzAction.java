package nablarch.common.authorization.role.action.sub;

import nablarch.common.authorization.role.CheckRole;

public class FizzAction {

    @CheckRole("FOO")
    public void publicMethod() {}
}
