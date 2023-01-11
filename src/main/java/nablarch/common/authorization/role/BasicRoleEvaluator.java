package nablarch.common.authorization.role;

import nablarch.fw.ExecutionContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link RoleEvaluator}の基本的な実装を提供するクラス。
 * <p>
 * このクラスは、{@link UserRoleResolver}を使ってユーザに紐づくロールの一覧を取得し、
 * そのロール一覧を用いて権限の有無を判定する。
 * </p>
 * @author Tanaka Tomoyuki
 */
public class BasicRoleEvaluator implements RoleEvaluator {
    private UserRoleResolver userRoleResolver;

    @Override
    public boolean evaluateAnyOf(String userId, Collection<String> roles, ExecutionContext context) {
        checkUserRoleResolverIsNotNull();

        Set<String> userRoles = new HashSet<String>(userRoleResolver.resolve(userId, context));
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean evaluateAllOf(String userId, Collection<String> roles, ExecutionContext context) {
        checkUserRoleResolverIsNotNull();

        Set<String> userRoles = new HashSet<String>(userRoleResolver.resolve(userId, context));
        for (String role : roles) {
            if (!userRoles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@link UserRoleResolver}が設定されていることを検証する。
     */
    private void checkUserRoleResolverIsNotNull() {
        if (userRoleResolver == null) {
            throw new IllegalStateException("UserRoleResolver is null.");
        }
    }

    /**
     * {@link UserRoleResolver}を設定する。
     * @param userRoleResolver {@link UserRoleResolver}のインスタンス
     */
    public void setUserRoleResolver(UserRoleResolver userRoleResolver) {
        this.userRoleResolver = userRoleResolver;
    }
}
