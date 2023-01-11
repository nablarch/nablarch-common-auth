package nablarch.common.authorization.role.action;

import nablarch.common.authorization.role.CheckRole;

import java.util.List;

public class FooAction extends ParentAction {

    public void publicMethodNoAnnotation() {
        // 対象
    }

    @CheckRole("FOO")
    public static void publicStaticMethod() {
        // staticなので対象外
    }

    @CheckRole("FOO")
    public void publicMethodWithAnnotationNoArgs() {
        // 対象
    }

    @CheckRole("FOO")
    public void publicMethodWithAnnotationSingleArg(String string) {
        // 対象
    }

    @CheckRole("FOO")
    public void publicMethodWithAnnotationMultipleArg(String string, int i, List<String> list) {
        // 対象
    }

    @CheckRole(value = {"FOO", "BAR", "FIZZ", "BUZZ"})
    public void publicMethodWithAnnotationMultipleRoles() {
        // 対象
    }

    @CheckRole(value = "FOO", anyOf = true)
    public void publicMethodWithAnnotationAnyOfTrue() {
        // 対象
    }

    @CheckRole("FOO")
    protected void protectedMethod() {
        // publicでないので対象外
    }

    @CheckRole("FOO")
    void packagePrivateMethod() {
        // publicでないので対象外
    }

    @CheckRole("FOO")
    private void privateMethod() {
        // publicでないので対象外
    }
}
