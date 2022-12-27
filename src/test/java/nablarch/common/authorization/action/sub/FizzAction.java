package nablarch.common.authorization.action.sub;

import nablarch.common.authorization.CheckAuthority;

public class FizzAction {

    @CheckAuthority("FOO")
    public void publicMethod() {}
}
