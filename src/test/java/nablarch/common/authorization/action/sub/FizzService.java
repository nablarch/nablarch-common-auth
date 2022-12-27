package nablarch.common.authorization.action.sub;

import nablarch.common.authorization.CheckAuthority;

public class FizzService {

    @CheckAuthority("FOO")
    public void publicMethod() {}
}
