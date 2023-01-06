package nablarch.common.authorization;

import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

import java.util.Collection;

/**
 * ユーザに権限があるか判定を行うインタフェース。
 * @author Tanaka Tomoyuki
 */
@Published(tag = "architect")
public interface AuthorityEvaluator {

    /**
     * 指定されたユーザが、指定された権限をいずれか1つでも有していることを判定する。
     * @param userId 判定対象のユーザID
     * @param authorities 権限の一覧
     * @param context 実行コンテキスト
     * @return 権限を有する場合は {@code true}
     */
    boolean evaluateAnyOf(String userId, Collection<String> authorities, ExecutionContext context);

    /**
     * 指定されたユーザが、指定された権限を全て有していることを判定する。
     * @param userId 判定対象のユーザID
     * @param authorities 権限の一覧
     * @param context 実行コンテキスト
     * @return 権限を有する場合は {@code true}
     */
    boolean evaluateAllOf(String userId, Collection<String> authorities, ExecutionContext context);
}
