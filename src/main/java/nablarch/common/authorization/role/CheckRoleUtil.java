package nablarch.common.authorization.role;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

import java.util.Collection;
import java.util.Collections;

/**
 * {@link RoleEvaluator}を用いたロール管理をプログラムから利用するためのユーティリティ。
 * <p>
 * 本クラスが提供するメソッドは、{@link ThreadContext#getUserId()} で取得したユーザIDを元に
 * 現在のアクセスユーザを特定する。
 * そして、そのアクセスユーザが指定されたロールを有するかどうかを判定する。
 * </p>
 * <p>
 * ロールの判定には {@link RoleEvaluator} を使用する。
 * このインスタンスは、システムリポジトリから {@code "roleEvaluator"} という名前で取得する。
 * </p>
 * @author Tanaka Tomoyuki
 */
@Published
public class CheckRoleUtil {
    /**
     * 現在のアクセスユーザが指定されたロールを有することを判定する。
     * @param role ロール
     * @param context 実行コンテキスト
     * @return ロールを有する場合は {@code true}
     */
    public static boolean checkRole(String role, ExecutionContext context) {
        return obtainRoleEvaluator()
                .evaluateAllOf(ThreadContext.getUserId(), Collections.singletonList(role), context);
    }

    /**
     * 現在のアクセスユーザが指定されたロールを全て有することを判定する。
     * @param roles ロールの一覧
     * @param context 実行コンテキスト
     * @return ロールを全て有する場合は {@code true}
     */
    public static boolean checkRoleAllOf(Collection<String> roles, ExecutionContext context) {
        return obtainRoleEvaluator()
                .evaluateAllOf(ThreadContext.getUserId(), roles, context);
    }

    /**
     * 現在のアクセスユーザが指定されたロールを1つでも有することを判定する。
     * @param roles ロールの一覧
     * @param context 実行コンテキスト
     * @return ロールを1つでも有する場合は {@code true}
     */
    public static boolean checkRoleAnyOf(Collection<String> roles, ExecutionContext context) {
        return obtainRoleEvaluator()
                .evaluateAnyOf(ThreadContext.getUserId(), roles, context);
    }

    /**
     * {@link RoleEvaluator}を取得する。
     * @return {@link RoleEvaluator}
     */
    private static RoleEvaluator obtainRoleEvaluator() {
        return SystemRepository.get("roleEvaluator");
    }

    /**
     * 本クラスはインスタンス化しない。
     */
    private CheckRoleUtil() {}
}
