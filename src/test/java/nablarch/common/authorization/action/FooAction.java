package nablarch.common.authorization.action;

import nablarch.common.authorization.CheckAuthority;

import java.util.List;

public class FooAction extends ParentAction {

    public void publicMethodNoAnnotation() {
        // 対象
    }

    @CheckAuthority("FOO")
    public static void publicStaticMethod() {
        // staticなので対象外
    }

    @CheckAuthority("FOO")
    public void publicMethodWithAnnotationNoArgs() {
        // 対象
    }

    @CheckAuthority("FOO")
    public void publicMethodWithAnnotationSingleArg(String string) {
        // 対象
    }

    @CheckAuthority("FOO")
    public void publicMethodWithAnnotationMultipleArg(String string, int i, List<String> list) {
        // 対象
    }

    @CheckAuthority(value = {"FOO", "BAR", "FIZZ", "BUZZ"})
    public void publicMethodWithAnnotationMultipleAuthorities() {
        // 対象
    }

    @CheckAuthority(value = "FOO", anyOf = true)
    public void publicMethodWithAnnotationAnyOfTrue() {
        // 対象
    }

    @CheckAuthority("FOO")
    protected void protectedMethod() {
        // publicでないので対象外
    }

    @CheckAuthority("FOO")
    void packagePrivateMethod() {
        // publicでないので対象外
    }

    @CheckAuthority("FOO")
    private void privateMethod() {
        // publicでないので対象外
    }
}
