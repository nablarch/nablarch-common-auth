package nablarch.common.authorization.role;

import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

import java.util.Collection;

/**
 * ユーザに紐づくロールの一覧を解決するインタフェース。
 *
 * @author Tanaka Tomoyuki
 */
@Published(tag = "architect")
public interface UserRoleResolver {

    /**
     * 指定されたユーザに紐づくロールの一覧を解決して返却する。
     * @param userId ユーザID
     * @param context 実行コンテキスト
     * @return ユーザに紐づくロールの一覧(ロールが無い場合は空のコレクションを返す)
     */
    Collection<String> resolve(String userId, ExecutionContext context);
}
