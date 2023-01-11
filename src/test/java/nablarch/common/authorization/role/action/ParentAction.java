package nablarch.common.authorization.role.action;

import nablarch.common.authorization.role.CheckRole;

public class ParentAction {

    @CheckRole("FOO")
    public void parentMethod() {}
}
