package nablarch.common.authorization;

import nablarch.fw.ExecutionContext;

import java.util.Collection;

/**
 * {@link AuthorityEvaluator}のモック。
 *
 * @author Tanaka Tomoyuki
 */
public class MockAuthorityEvaluator implements AuthorityEvaluator {
    /**
     * 各メソッドの戻り値。
     */
    public boolean returnValue;
    /**
     * 各メソッドに渡されたユーザID。
     */
    public String userId;
    /**
     * 各メソッドに渡された権限の一覧。
     */
    public Collection<String> authorities;
    /**
     * 各メソッドに渡された実行コンテキスト。
     */
    public ExecutionContext context;
    /**
     * 実行されたメソッドの名前。
     */
    public String calledMethodName;

    @Override
    public boolean evaluateAnyOf(String userId, Collection<String> authorities, ExecutionContext context) {
        this.userId = userId;
        this.authorities = authorities;
        this.context = context;
        calledMethodName = "evaluateAnyOf";
        return returnValue;
    }

    @Override
    public boolean evaluateAllOf(String userId, Collection<String> authorities, ExecutionContext context) {
        this.userId = userId;
        this.authorities = authorities;
        this.context = context;
        calledMethodName = "evaluateAllOf";
        return returnValue;
    }
}