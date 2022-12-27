package nablarch.common.authorization;

import nablarch.fw.ExecutionContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link AuthorityEvaluator}の基本的な実装を提供するクラス。
 * <p>
 * このクラスは、{@link UserAuthorityResolver}を使ってユーザに紐づく権限の一覧を取得し、
 * その権限一覧を用いて権限の有無を判定する。
 * </p>
 * @author Tanaka Tomoyuki
 */
public class BasicAuthorityEvaluator implements AuthorityEvaluator {
    private UserAuthorityResolver userAuthorityResolver;

    @Override
    public boolean evaluateAnyOf(String userId, Collection<String> authorities, ExecutionContext context) {
        checkUserAuthorityResolverIsNotNull();

        Set<String> usersAuthorities = new HashSet<String>(userAuthorityResolver.resolve(userId, context));
        for (String authority : authorities) {
            if (usersAuthorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean evaluateAllOf(String userId, Collection<String> authorities, ExecutionContext context) {
        checkUserAuthorityResolverIsNotNull();

        Set<String> usersAuthorities = new HashSet<String>(userAuthorityResolver.resolve(userId, context));
        for (String authority : authorities) {
            if (!usersAuthorities.contains(authority)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@link UserAuthorityResolver}が設定されていることを検証する。
     */
    private void checkUserAuthorityResolverIsNotNull() {
        if (userAuthorityResolver == null) {
            throw new IllegalStateException("UserAuthorityResolver is null.");
        }
    }

    /**
     * {@link UserAuthorityResolver}を設定する。
     * @param userAuthorityResolver {@link UserAuthorityResolver}のインスタンス
     */
    public void setUserAuthorityResolver(UserAuthorityResolver userAuthorityResolver) {
        this.userAuthorityResolver = userAuthorityResolver;
    }
}
