package nablarch.common.permission;

import java.util.SortedSet;

import nablarch.core.util.annotation.Published;

/**
 * 認可を行うインタフェース。<br>
 * <br>
 * 認可判定の実現方法毎に本インタフェースの実装クラスを作成する。
 * 
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public interface Permission {

    /**
     * リクエストIDを認可判定する。
     * 
     * @param requestId リクエストID
     * @return 認可に成功した場合は<code>true</code>、認可に失敗した場合は<code>false</code>
     */
    boolean permit(String requestId);
    
    /**
     * ユーザに許可されたリクエストIDを取得する。
     * 
     * @return ユーザに許可されたリクエストID
     */
    SortedSet<String> getRequestIds();
}
