package nablarch.common.authorization.role;

import nablarch.fw.ExecutionContext;

import java.util.Collection;

/**
 * {@link RoleEvaluator}のモック。
 *
 * @author Tanaka Tomoyuki
 */
public class MockRoleEvaluator implements RoleEvaluator {
    /**
     * 各メソッドの戻り値。
     */
    public boolean returnValue;
    /**
     * 各メソッドに渡されたユーザID。
     */
    public String userId;
    /**
     * 各メソッドに渡されたロールの一覧。
     */
    public Collection<String> roles;
    /**
     * 各メソッドに渡された実行コンテキスト。
     */
    public ExecutionContext context;
    /**
     * 実行されたメソッドの名前。
     */
    public String calledMethodName;

    @Override
    public boolean evaluateAnyOf(String userId, Collection<String> roles, ExecutionContext context) {
        this.userId = userId;
        this.roles = roles;
        this.context = context;
        calledMethodName = "evaluateAnyOf";
        return returnValue;
    }

    @Override
    public boolean evaluateAllOf(String userId, Collection<String> roles, ExecutionContext context) {
        this.userId = userId;
        this.roles = roles;
        this.context = context;
        calledMethodName = "evaluateAllOf";
        return returnValue;
    }
}