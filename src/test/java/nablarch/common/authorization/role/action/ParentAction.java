package nablarch.common.authorization.role.action;

import nablarch.common.authorization.role.CheckAuthority;

public class ParentAction {

    @CheckAuthority("FOO")
    public void parentMethod() {}
}
