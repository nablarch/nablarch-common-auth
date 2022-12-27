package nablarch.common.authorization;

import nablarch.fw.ExecutionContext;

import java.util.Collection;

/**
 * ユーザに紐づく権限の一覧を解決するインタフェース。
 *
 * @author Tanaka Tomoyuki
 */
public interface UserAuthorityResolver {

    /**
     * 指定されたユーザに紐づく権限の一覧を解決して返却する。
     * @param userId ユーザID
     * @param context 実行コンテキスト
     * @return ユーザに紐づく権限の一覧(権限が無い場合は空のコレクションを返す)
     */
    Collection<String> resolve(String userId, ExecutionContext context);
}
