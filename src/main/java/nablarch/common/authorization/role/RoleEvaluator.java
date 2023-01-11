package nablarch.common.authorization.role;

import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

import java.util.Collection;

/**
 * ユーザにロールがあるか判定を行うインタフェース。
 * @author Tanaka Tomoyuki
 */
@Published(tag = "architect")
public interface RoleEvaluator {

    /**
     * 指定されたユーザが、指定されたロールをいずれか1つでも有していることを判定する。
     * @param userId 判定対象のユーザID
     * @param roles ロールの一覧
     * @param context 実行コンテキスト
     * @return ロールを有する場合は {@code true}
     */
    boolean evaluateAnyOf(String userId, Collection<String> roles, ExecutionContext context);

    /**
     * 指定されたユーザが、指定されたロールを全て有していることを判定する。
     * @param userId 判定対象のユーザID
     * @param roles ロールの一覧
     * @param context 実行コンテキスト
     * @return ロールを有する場合は {@code true}
     */
    boolean evaluateAllOf(String userId, Collection<String> roles, ExecutionContext context);
}
