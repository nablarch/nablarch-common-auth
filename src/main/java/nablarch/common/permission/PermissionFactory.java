package nablarch.common.permission;

import nablarch.core.util.annotation.Published;

/**
 * {@link Permission}を生成するインタフェース。
 * <br>
 * 認可情報の取得先毎に本インタフェースの実装クラスを作成する。
 * 
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public interface PermissionFactory {
    
    /**
     * {@link Permission}を取得する。
     * 
     * @param userId ユーザID
     * @return {@link Permission}
     */
    Permission getPermission(String userId);
}
