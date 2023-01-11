package nablarch.common.authorization.role;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

import java.util.Collection;
import java.util.Collections;

/**
 * {@link AuthorityEvaluator}を用いた権限管理をプログラムから利用するためのユーティリティ。
 * <p>
 * 本クラスが提供するメソッドは、{@link ThreadContext#getUserId()} で取得したユーザIDを元に
 * 現在のアクセスユーザを特定する。
 * そして、そのアクセスユーザが指定された権限を有するかどうかを判定する。
 * </p>
 * <p>
 * 権限の判定には {@link AuthorityEvaluator} を使用する。
 * このインスタンスは、システムリポジトリから {@code "authorityEvaluator"} という名前で取得する。
 * </p>
 * @author Tanaka Tomoyuki
 */
@Published
public class AuthorityUtil {
    /**
     * 現在のアクセスユーザが指定された権限を有することを判定する。
     * @param authority 権限
     * @param context 実行コンテキスト
     * @return 権限を有する場合は {@code true}
     */
    public static boolean checkAuthority(String authority, ExecutionContext context) {
        return obtainAuthorityEvaluator()
                .evaluateAllOf(ThreadContext.getUserId(), Collections.singletonList(authority), context);
    }

    /**
     * 現在のアクセスユーザが指定された権限を全て有することを判定する。
     * @param authorities 権限の一覧
     * @param context 実行コンテキスト
     * @return 権限を全て有する場合は {@code true}
     */
    public static boolean checkAuthorityAllOf(Collection<String> authorities, ExecutionContext context) {
        return obtainAuthorityEvaluator()
                .evaluateAllOf(ThreadContext.getUserId(), authorities, context);
    }

    /**
     * 現在のアクセスユーザが指定された権限を1つでも有することを判定する。
     * @param authorities 権限の一覧
     * @param context 実行コンテキスト
     * @return 権限を1つでも有する場合は {@code true}
     */
    public static boolean checkAuthorityAnyOf(Collection<String> authorities, ExecutionContext context) {
        return obtainAuthorityEvaluator()
                .evaluateAnyOf(ThreadContext.getUserId(), authorities, context);
    }

    /**
     * {@link AuthorityEvaluator}を取得する。
     * @return {@link AuthorityEvaluator}
     */
    private static AuthorityEvaluator obtainAuthorityEvaluator() {
        return SystemRepository.get("authorityEvaluator");
    }

    /**
     * 本クラスはインスタンス化しない。
     */
    private AuthorityUtil() {}
}
