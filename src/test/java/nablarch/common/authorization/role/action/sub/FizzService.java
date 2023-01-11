package nablarch.common.authorization.role.action.sub;

import nablarch.common.authorization.role.CheckAuthority;

public class FizzService {

    @CheckAuthority("FOO")
    public void publicMethod() {}
}
