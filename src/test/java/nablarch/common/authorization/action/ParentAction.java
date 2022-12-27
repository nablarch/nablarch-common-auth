package nablarch.common.authorization.action;

import nablarch.common.authorization.CheckAuthority;

public class ParentAction {

    @CheckAuthority("FOO")
    public void parentMethod() {}
}
